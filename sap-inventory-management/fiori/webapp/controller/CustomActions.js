sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "sap/m/MessageToast",
    "sap/m/Dialog",
    "sap/m/Button",
    "sap/m/VBox",
    "sap/m/Label",
    "sap/m/Input",
    "sap/m/Text",
    "sap/ui/model/json/JSONModel"
], function (Controller, MessageToast, Dialog, Button, VBox, Label, Input, Text, JSONModel) {
    "use strict";

    return Controller.extend("com.company.inventorymanagement.ext.actions.CustomActions", {

        /**
         * Custom action: Post Goods Receipt
         * Triggered from List Report table toolbar
         */
        postGoodsReceipt: function (oBindingContext, aSelectedContexts) {
            const oController = this;
            const oI18n = this.getModel("i18n").getResourceBundle();

            if (!aSelectedContexts || aSelectedContexts.length === 0) {
                MessageToast.show(oI18n.getText("msgNoStock"));
                return;
            }

            // GR Dialog
            const oDialogModel = new JSONModel({
                quantity: 0,
                referenceDoc: "",
                notes: ""
            });

            const oDialog = new Dialog({
                title: oI18n.getText("dlgGoodsReceipt"),
                content: new VBox({
                    items: [
                        new Label({ text: oI18n.getText("dlgQty"), required: true }),
                        new Input({
                            value: "{dialogModel>/quantity}",
                            type: "Number",
                            placeholder: "0.000"
                        }),
                        new Label({ text: oI18n.getText("dlgRefDoc") }),
                        new Input({
                            value: "{dialogModel>/referenceDoc}",
                            placeholder: "e.g. 4500001234"
                        }),
                        new Label({ text: oI18n.getText("dlgNotes") }),
                        new Input({
                            value: "{dialogModel>/notes}",
                            placeholder: oI18n.getText("dlgNotes")
                        })
                    ]
                }).addStyleClass("sapUiSmallMargin"),
                beginButton: new Button({
                    text: oI18n.getText("postGoodsReceipt"),
                    type: "Emphasized",
                    press: async function () {
                        const oDialogData = oDialogModel.getData();

                        if (!oDialogData.quantity || oDialogData.quantity <= 0) {
                            MessageToast.show("Please enter a valid quantity.");
                            return;
                        }

                        try {
                            // Call RAP action via OData V4
                            for (const oContext of aSelectedContexts) {
                                await oContext.executeAction("ZINV_SRV.PostGoodsReceipt", {
                                    quantity: parseFloat(oDialogData.quantity),
                                    reference_doc: oDialogData.referenceDoc,
                                    notes: oDialogData.notes
                                });
                            }

                            MessageToast.show(
                                oI18n.getText("msgGRPosted", [aSelectedContexts.length])
                            );
                            oDialog.close();

                        } catch (oError) {
                            MessageToast.show(
                                oI18n.getText("msgValidationError", [oError.message])
                            );
                        }
                    }
                }),
                endButton: new Button({
                    text: oI18n.getText("cancel"),
                    press: function () { oDialog.close(); }
                }),
                afterClose: function () { oDialog.destroy(); }
            });

            oDialog.setModel(oDialogModel, "dialogModel");
            oController.getView().addDependent(oDialog);
            oDialog.open();
        },

        /**
         * Custom action: Post Goods Issue
         */
        postGoodsIssue: function (oBindingContext, aSelectedContexts) {
            const oController = this;
            const oI18n = this.getModel("i18n").getResourceBundle();

            if (!aSelectedContexts || aSelectedContexts.length === 0) {
                MessageToast.show(oI18n.getText("msgNoStock"));
                return;
            }

            const oDialogModel = new JSONModel({ quantity: 0, referenceDoc: "", notes: "" });

            const oDialog = new Dialog({
                title: oI18n.getText("dlgGoodsIssue"),
                content: new VBox({
                    items: [
                        new Label({ text: oI18n.getText("dlgQty"), required: true }),
                        new Input({
                            value: "{dialogModel>/quantity}",
                            type: "Number",
                            placeholder: "0.000"
                        }),
                        new Label({ text: oI18n.getText("dlgRefDoc") }),
                        new Input({
                            value: "{dialogModel>/referenceDoc}",
                            placeholder: "e.g. Order / Cost Center"
                        }),
                        new Label({ text: oI18n.getText("dlgNotes") }),
                        new Input({ value: "{dialogModel>/notes}" })
                    ]
                }).addStyleClass("sapUiSmallMargin"),
                beginButton: new Button({
                    text: oI18n.getText("postGoodsIssue"),
                    type: "Emphasized",
                    press: async function () {
                        const oDialogData = oDialogModel.getData();
                        try {
                            for (const oContext of aSelectedContexts) {
                                await oContext.executeAction("ZINV_SRV.PostGoodsIssue", {
                                    quantity: parseFloat(oDialogData.quantity),
                                    reference_doc: oDialogData.referenceDoc,
                                    notes: oDialogData.notes
                                });
                            }
                            MessageToast.show(oI18n.getText("msgGIPosted", [aSelectedContexts.length]));
                            oDialog.close();
                        } catch (oError) {
                            MessageToast.show(oError.message);
                        }
                    }
                }),
                endButton: new Button({
                    text: oI18n.getText("cancel"),
                    press: function () { oDialog.close(); }
                }),
                afterClose: function () { oDialog.destroy(); }
            });

            oDialog.setModel(oDialogModel, "dialogModel");
            oController.getView().addDependent(oDialog);
            oDialog.open();
        },

        /**
         * Joule AI integration - ask questions about inventory
         * Uses BTP AI Core / Joule API
         */
        askJoule: async function (sPrompt) {
            const oJouleService = this._getJouleService();

            const oPayload = {
                messages: [
                    {
                        role: "system",
                        content: `You are an SAP inventory management assistant. 
                                  Help users understand stock levels, suggest reorder actions, 
                                  and explain inventory movements. 
                                  Always provide concise, actionable answers.`
                    },
                    {
                        role: "user",
                        content: sPrompt
                    }
                ],
                model: "gpt-4",
                max_tokens: 500
            };

            try {
                const oResponse = await oJouleService.complete(oPayload);
                return oResponse.choices[0].message.content;
            } catch (oError) {
                console.error("Joule API error:", oError);
                throw oError;
            }
        },

        _getJouleService: function () {
            // Returns the Joule / BTP AI Core service proxy
            // Configured via BTP Destination: JOULE_AI_SERVICE
            return this.getModel("joule");
        }
    });
});
