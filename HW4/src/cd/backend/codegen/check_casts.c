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
*   currentObject: The pointer to the object to cast.
*/
void cast(void *typeToCast, void **currentObject){
//    printf("TypeToCast§le: 0x%x, currentType: 0x%x", typeToCast, currentType);

    // If current object is a null pointer (e.g. 0) don't try to estimate the type. Would cause a segfault.
    if(!currentObject){
        return;
    }

    // check if super type is same as type to cast.
    while(currentObject != 0){
        if(typeToCast == currentObject){
            // If currentObject is a subtype of typeToCast everything is ok and return.
            return;
        }

        // Get supertype
        currentObject = *currentObject;
    }

    // Was not a subtype throw error
    __asm__("jmp {0}");
}