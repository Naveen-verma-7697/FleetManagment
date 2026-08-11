package com.fleman.dto;

// Separate from CustomerDTO on purpose - the base64 payload is only
// fetched when something actually needs to render/download it (the
// profile page's "view uploaded ID" action), not on every customer read.
public class GovtIdDocumentDTO {
    private String govtIdDocument;
    private String govtIdDocumentName;
    private String govtIdDocumentType;

    public GovtIdDocumentDTO() {}

    public GovtIdDocumentDTO(String govtIdDocument, String govtIdDocumentName, String govtIdDocumentType) {
        this.govtIdDocument = govtIdDocument;
        this.govtIdDocumentName = govtIdDocumentName;
        this.govtIdDocumentType = govtIdDocumentType;
    }

    public String getGovtIdDocument() { return govtIdDocument; }
    public void setGovtIdDocument(String govtIdDocument) { this.govtIdDocument = govtIdDocument; }
    public String getGovtIdDocumentName() { return govtIdDocumentName; }
    public void setGovtIdDocumentName(String govtIdDocumentName) { this.govtIdDocumentName = govtIdDocumentName; }
    public String getGovtIdDocumentType() { return govtIdDocumentType; }
    public void setGovtIdDocumentType(String govtIdDocumentType) { this.govtIdDocumentType = govtIdDocumentType; }
}
