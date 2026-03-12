sap.ui.define([
    "sap/ui/core/UIComponent",
    "sap/ui/model/json/JSONModel"
], function (UIComponent, JSONModel) {
    "use strict";
    return UIComponent.extend("com.company.inventorymanagement.Component", {
        metadata: {
            manifest: "json"
        },
        init: function () {
            UIComponent.prototype.init.apply(this, arguments);
            var oStockModel = new JSONModel();
            oStockModel.loadData("localService/mockdata/Stock.json");
            this.setModel(oStockModel, "stock");
            var oMovementModel = new JSONModel();
            oMovementModel.loadData("localService/mockdata/Movement.json");
            this.setModel(oMovementModel, "movements");
        }
    });
});
