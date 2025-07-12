export function getItemsFromAreas(areas){
    return areas.data.map(getItemFromArea)
}

export function getItemFromArea(area){
    return {
        name: area.storageAreaName, // Area name
        id: area.storageAreaId || null, // Area ID
        isArchived: area.storageAreaArchived || false
    }
}

export function getAreaFromItem(item){
    return {
        storageAreaName: item.name, // Area name
        storageAreaId: item.id, // Area ID
        storageAreaArchived: item.isArchived
    }
}

export function getAreaFromName(name){
    return {
        storageAreaName: name, // Area name
        storageAreaArchived: false
    }
}