export const INVENTORY_MESSAGES = {
  MISSING_FIELD_TEXT: "Des éléments supplémentaires sont necessaires pour enregistrer la saisie de stock",
  REFERENCE_NAME_MISSING: "Veuillez indiquer le nom de la référence avant de valider",
  MODIFICATION_VALIDATION: 'Modification validée',
  LAST_STOCK_REGISTRATION_ADD_SUCCESS: "Dernière saisie de stock connue ajoutée avec succés",
  NEW_REFERENCE: "Nouvelle référence",
};

export const MISSING_FIELDS = {
  MISSING_CATEGORY: "category",
}

export const getInvalidQuantityMessage = (referenceName) => `Quantité invalide pour la référence ${referenceName}`

export const getReferenceAddDuringInventoryProcessMessage = (referenceName, areaName) => `Référence ${referenceName} ajoutée à la zone ${areaName} avec succés`

export const getRemindUpdateReferenceMessage = (referenceName) => `N'oubliez pas d'actualiser les informations de la référence ${referenceName} ` +
  `dans l'administration des références avant la génération du rapport`

export const getStockRegistrationForReferenceMessageInArea = (referenceName, areaName) => `Enregistrement de stock pour la référence ${referenceName} dans la zone ${areaName}`