import {ref} from "vue";
import type {VerificationResult} from "~/generated/system";

export const useFormValidation = () => {
    const errData = ref<any>({});
    const validate = (data: VerificationResult[]) => {
        data.forEach((item: any) => {
            errData.value[item.field] = item.message
        })
    }
    const resetValidate = () => {
        errData.value = {}
    }
    return {
        errData,
        validate,
        resetValidate,
    }
}