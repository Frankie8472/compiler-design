package cd.backend.codegen;

import java.util.*;

/**
 * Simple class that manages the set of currently used
 * and unused registers
 */
public class RegisterManager {
	private List<Register> registers = new ArrayList<Register>();

	private Map<String, Register> registerTags = new HashMap<>();

	// lists of register to save by the callee and the caller
	public static final Register CALLEE_SAVE[] = new Register[]{Register.ESI,
			Register.EDI, Register.EBX};
	public static final Register CALLER_SAVE[] = new Register[]{Register.EAX,
			Register.ECX, Register.EDX};
	
	// list of general purpose registers
	public static final Register GPR[] = new Register[]{Register.EAX, Register.EBX,
		Register.ECX, Register.EDX, Register.ESI, Register.EDI};

	// special purpose registers
	public static final Register BASE_REG = Register.EBP;
	public static final Register STACK_REG = Register.ESP;

	public static final int SIZEOF_REG = 4;

	
	public enum Register {
		EAX("%eax", ByteRegister.EAX), EBX("%ebx", ByteRegister.EBX), ECX(
				"%ecx", ByteRegister.ECX), EDX("%edx", ByteRegister.EDX), ESI(
				"%esi", null), EDI("%edi", null), EBP("%ebp", null), ESP(
				"%esp", null);

		public final String repr;
		private final ByteRegister lowByteVersion;

		private Register(String repr, ByteRegister bv) {
			this.repr = repr;
			this.lowByteVersion = bv;
		}

		@Override
		public String toString() {
			return repr;
		}

		/**
		 * determines if this register has an 8bit version
		 */
		public boolean hasLowByteVersion() {
			return lowByteVersion != null;
		}

		/**
		 * Given a register like {@code %eax} returns {@code %al}, but doesn't
		 * work for {@code %esi} and {@code %edi}!
		 */
		public ByteRegister lowByteVersion() {
			assert hasLowByteVersion();
			return lowByteVersion;
		}
	}

	public enum ByteRegister {
		EAX("%al"), EBX("%bl"), ECX("%cl"), EDX("%dl");

		public final String repr;

		private ByteRegister(String repr) {
			this.repr = repr;
		}

		@Override
		public String toString() {
			return repr;
		}
	}

	/**
	 * Reset all general purpose registers to free
	 */
	public void initRegisters() {
		registers.clear();
		registers.addAll(Arrays.asList(GPR));
		registerTags.clear();
	}

	/**
	 * returns a free register and marks it as used
	 */
	public Register getRegister() {
		int availableRegisters = availableRegisters();
		if(availableRegisters <=0){
			throw new AssemblyFailedException(
					"Program requires too many registers");
		}

		// Select register which is not tagged. If not possible select last in registers List.
		Register newReg;
		if(availableRegisters - registerTags.keySet().size() > 0){
		    List<Register> filtered = new ArrayList<>();
			for(Register reg : registers){
			    if(!registerTags.values().contains(reg)){
			        filtered.add(reg);
                }
            }
		    newReg = filtered.get(filtered.size() -1);
			registers.remove(newReg);
		} else {
			newReg = registers.remove(registers.size() - 1);
            removeRegisterTag(newReg);
        }

		return newReg;
	}

	/**
	 * marks a currently used register as free
	 */
	public void releaseRegister(Register reg) {
		assert !registers.contains(reg);
		registers.add(reg);
	}

	public void setRegisterUsed(Register reg){
//		assert registers.contains(reg);
		registers.remove(reg);
	}

	/**
	 * Returns whether the register is currently non-free
	 */
	public boolean isInUse(Register reg) {
		return !registers.contains(reg);
	}

	/**
	 * returns the number of free registers
	 */
	public int availableRegisters() {
		return registers.size();
	}

	public void tagRegister(Register reg, String tag){
		registerTags.put(tag, reg);
	}

	public void removeRegisterTag(Register reg){
	    String removeTag = null;
		for(String tag : registerTags.keySet()){
		    if(reg == registerTags.get(tag)){
		        removeTag = tag;
		        break;
            }
        }
        if(removeTag != null) {
            registerTags.remove(removeTag);
        }
	}

	public void removeTagFromRegsiter(String tag){
	    registerTags.remove(tag);
    }

	public Register getRegisterFromTag(String tag){
		return registerTags.get(tag);
	}

	public void flushTags(){
	    registerTags.clear();
    }
}