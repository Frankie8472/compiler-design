#include<stdlib.h>

// The Method_VTable of Object.
extern void *Object_method_table;
extern int CAST_ERROR_CODE;
//#define CAST_ERROR_CODE 0
//extern void *A_method_table;
//extern void *B_method_table;
//extern void *Main_method_table;

/**
* Tests if a cast is fine or dies with exit(1) otherwise
*
* Here 'Type' means the pointer to the Method_VTable. The Method_VTable of the superclass
*   lies at index 0 from the current Pointer. Object itself has no superclass and thus 0 as 
*   Pointer.
*
* Params:
*   typeToCast: Type the object should be casted to
*   currentType: The Current Type of the object
*/
void cast(void *typeToCast, void **currentType){
    //printf("As_tab§le: 0x%x, BsTable 0x%x, ObjectsTable 0x%x, , MainsTable 0x%x\n",&A_method_table, &B_method_table, &Object_method_table, &Main_method_table);
    // If current Object is an array
//    if(((int)currentType) & 1){
//        if(currentType == typeToCast){
//            // Must be the array of same type
//            return;
//        } else if (typeToCast == Object_method_table){
//            // Cast to Object OK
//            return;
//        } else {
//            exit(CAST_ERROR_CODE);
//        }
//    }
    // Current Object is not an array. Test if it's runtime type is a subtype of the cast;
    while(currentType != 0){
        if(typeToCast == currentType){
            return;
        }

        currentType = *currentType;
    }

    // Was not a subtype throw error
    exit(CAST_ERROR_CODE);
}