export const ADMIN_MESSAGES = {
  EMPTY_AREA_NAME: "Le nom de la zone ne peut pas être vide.",
  EMPTY_CATEGORY_NAME: "Le nom de la catégorie ne peut pas être vide.",
  ADD_AREA_SUCCESS: "Nouvelle zone de stockage ajoutée",
  ADD_CATEGORY_SUCCESS: "Nouvelle catégorie ajoutée",
  ADD_REFERENCE_SUCCESS: "Nouvelle référence ajoutée",
  STATUS_MODIFICATION_SUCCESS: "Modification de statut validé",
  REFERENCE_MODIFICATION_SUCCESS: "Référence modifiée avec succés",
};

export const ADMIN_VIEW_TEXT = {
  ACTIVE_STORAGE_LIST: 'Liste des zones de stockage actives',
  ARCHIVE_STORAGE_LIST: 'Liste des zones de stockage archivées',
  ACTIVE_CATEGORY_LIST: 'Liste des catégories actives',
  ARCHIVE_CATEGORY_LIST: 'Liste des catégories archivées',
  ACTIVE_REFERENCE_LIST: 'Liste des références actives',
  ARCHIVE_REFERENCE_LIST: 'Liste des références archivées',
  ZONE_TYPE: 'zone',
  CATEGORY_TYPE: "catégorie",
  REFERENCE_TYPE : "référence",
  ARCHIVE_BUTTON: 'Archiver',
  ACTIVE_BUTTON: 'Activer',
}

export const getPlaceHolderAddForm = (objectType) => "Nom de la " + objectType;
export const getPlaceHolderSearchForm = (objectType) => "Rechercher une " + objectType;