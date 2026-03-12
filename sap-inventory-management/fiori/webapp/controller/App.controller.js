sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "sap/m/MessageToast",
    "sap/m/MessageBox",
    "sap/m/Dialog",
    "sap/m/Button",
    "sap/m/VBox",
    "sap/m/Label",
    "sap/m/Input",
    "sap/m/Text"
], function (Controller, MessageToast, MessageBox, Dialog, Button, VBox, Label, Input, Text) {
    "use strict";

    return Controller.extend("com.company.inventorymanagement.controller.App", {

        onInit: function () {
            MessageToast.show("✅ Inventory Management loaded!", { duration: 2000 });
        },

        onSearch: function (oEvent) {
            var sQuery = oEvent.getParameter("query").toLowerCase();
            var oTable = this.getView().byId("stockTable");
            oTable.getItems().forEach(function(oItem) {
                var sText = oItem.getCells().map(function(c) {
                    return (c.getText ? c.getText() : (c.getTitle ? c.getTitle() : "")).toLowerCase();
                }).join(" ");
                oItem.setVisible(sText.indexOf(sQuery) !== -1);
            });
        },

        onStockItemPress: function (oEvent) {
            var oCells = oEvent.getSource().getCells();
            MessageBox.information(
                "Material:         " + oCells[0].getTitle()  + "\n" +
                "Plant:            " + oCells[1].getText()   + "\n" +
                "Storage Location: " + oCells[2].getText()   + "\n" +
                "Stock Type:       " + oCells[3].getText()   + "\n" +
                "Quantity:         " + oCells[4].getNumber() + " " + oCells[5].getText() + "\n" +
                "Status:           " + oCells[6].getText(),
                { title: "📦 Stock Details - " + oCells[0].getTitle() }
            );
        },

        _updateStock: function (sMaterialId, nDelta) {
            var oModel = this.getView().getModel("stock");
            var aStock = oModel.getData();
            var oItem  = aStock.find(function(s) { return s.MaterialId === sMaterialId; });

            if (!oItem) {
                MessageToast.show("⚠️ Material '" + sMaterialId + "' nu exista in stoc!");
                return false;
            }

            var nNou = oItem.Quantity + nDelta;
            if (nNou < 0) {
                MessageBox.error(
                    "Stoc insuficient!\nDisponibil: " + oItem.Quantity + " EA\nSolicitat:  " + Math.abs(nDelta) + " EA",
                    { title: "❌ Insufficient Stock" }
                );
                return false;
            }

            oItem.Quantity   = nNou;
            oItem.StockStatus = nNou === 0 ? "E" : "S";
            oModel.setData(aStock);
            return true;
        },

        onPostGR: function () {
            var oView    = this.getView();
            var oCtrl    = this;
            var oInputMat = new Input({ placeholder: "e.g. MAT-LAPTOP-001" });
            var oInputQty = new Input({ type: "Number", placeholder: "e.g. 50" });
            var oInputRef = new Input({ placeholder: "e.g. PO4500001234" });

            var oDialog = new Dialog({
                title: "📥 Post Goods Receipt",
                content: new VBox({
                    items: [
                        new Text({ text: "Intrare marfa — stocul va CRESTE" }).addStyleClass("sapUiSmallMargin"),
                        new Label({ text: "Material ID", required: true }),
                        oInputMat,
                        new Label({ text: "Quantity", required: true }),
                        oInputQty,
                        new Label({ text: "Reference PO Number" }),
                        oInputRef
                    ]
                }).addStyleClass("sapUiSmallMargin"),
                beginButton: new Button({
                    text: "✅ Post GR",
                    type: "Emphasized",
                    press: function () {
                        var sMat = oInputMat.getValue().trim();
                        var nQty = parseFloat(oInputQty.getValue());
                        if (!sMat || !nQty || nQty <= 0) {
                            MessageToast.show("⚠️ Completeaza Material si Quantity!");
                            return;
                        }
                        var bOk = oCtrl._updateStock(sMat, +nQty);
                        if (bOk) {
                            MessageBox.success(
                                "Material: " + sMat + "\nCantitate adaugata: +" + nQty + " EA\nStocul a fost actualizat in tabel!",
                                { title: "✅ Goods Receipt Posted" }
                            );
                            oDialog.close();
                        }
                    }
                }),
                endButton: new Button({
                    text: "Cancel",
                    press: function () { oDialog.close(); }
                }),
                afterClose: function () { oDialog.destroy(); }
            });
            oView.addDependent(oDialog);
            oDialog.open();
        },

        onPostGI: function () {
            var oView     = this.getView();
            var oCtrl     = this;
            var oInputMat = new Input({ placeholder: "e.g. MAT-LAPTOP-001" });
            var oInputQty = new Input({ type: "Number", placeholder: "e.g. 10" });
            var oInputRef = new Input({ placeholder: "e.g. SO5000009876" });

            var oDialog = new Dialog({
                title: "📤 Post Goods Issue",
                content: new VBox({
                    items: [
                        new Text({ text: "Iesire marfa — stocul va SCADEA" }).addStyleClass("sapUiSmallMargin"),
                        new Label({ text: "Material ID", required: true }),
                        oInputMat,
                        new Label({ text: "Quantity", required: true }),
                        oInputQty,
                        new Label({ text: "Reference SO/Order Number" }),
                        oInputRef
                    ]
                }).addStyleClass("sapUiSmallMargin"),
                beginButton: new Button({
                    text: "✅ Post GI",
                    type: "Emphasized",
                    press: function () {
                        var sMat = oInputMat.getValue().trim();
                        var nQty = parseFloat(oInputQty.getValue());
                        if (!sMat || !nQty || nQty <= 0) {
                            MessageToast.show("⚠️ Completeaza Material si Quantity!");
                            return;
                        }
                        var bOk = oCtrl._updateStock(sMat, -nQty);
                        if (bOk) {
                            MessageBox.warning(
                                "Material: " + sMat + "\nCantitate scazuta: -" + nQty + " EA\nStocul a fost actualizat in tabel!",
                                { title: "✅ Goods Issue Posted" }
                            );
                            oDialog.close();
                        }
                    }
                }),
                endButton: new Button({
                    text: "Cancel",
                    press: function () { oDialog.close(); }
                }),
                afterClose: function () { oDialog.destroy(); }
            });
            oView.addDependent(oDialog);
            oDialog.open();
        }
    });
});
