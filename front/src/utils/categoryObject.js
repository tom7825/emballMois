export function getItemsFromCategories(response){
    return response.data.map(getItemFromCategory)
}

export function getItemFromCategory(data){
    return {
        name: data.categoryName,
        id: data.categoryId || null,
        isArchived: data.categoryArchived || false
    }
}

export function getCategoryFromItem(item){
    return {
        categoryName: item.name,
        categoryId: item.id,
        categoryArchived: item.isArchived
    }
}

export function getCategoryObjectFromName(name){
    return {
        categoryName: name,
        categoryArchived: false
    }
}