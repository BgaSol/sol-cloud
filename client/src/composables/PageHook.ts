import {Ref} from "vue";

export interface PageDto {
    page: number;
    size: number;
}

export const usePage = <T extends PageDto>(pageData: Ref<T>, getTable: () => Promise<unknown>) => {
    /**
     * 页码变化
     * @param val
     */
    const handleCurrentChange = async (val: number) => {
        pageData.value.page = val;
        await getTable();
        // 滚动页面到顶部
        window.scrollTo(0, 0);
    };

    /**
     * 每页条数变化
     * @param val
     */
    const handleSizeChange = async (val: number) => {
        pageData.value.size = val;
        pageData.value.page = 1;
        await getTable();
        // 滚动页面到顶部
        window.scrollTo(0, 0);
    };

    /**
     * 搜索
     */
    const search = async () => {
        pageData.value.page = 1;
        await getTable();
    };

    /**
     * 重置分页数据
     * @param defaultRequestData
     */
    const resetPageData = async (defaultRequestData: T) => {
        pageData.value = defaultRequestData;
        await getTable();
    };

    return {
        handleCurrentChange,
        handleSizeChange,
        search,
        resetPageData
    }
}