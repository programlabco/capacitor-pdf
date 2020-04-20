package com.programlab.capacitor_pdf.models;

public class PdfOptions {
    private String linkPdf;
    private PdfAnnotations annotations;

    public PdfOptions(String linkPdf, PdfAnnotations annotations) {
        this.linkPdf = linkPdf;
        this.annotations = annotations;
    }

    public String getLinkPdf() {
        return linkPdf;
    }

    public void setLinkPdf(String linkPdf) {
        this.linkPdf = linkPdf;
    }

    public PdfAnnotations getAnnotations() {
        return annotations;
    }

    public void setAnnotations(PdfAnnotations annotations) {
        this.annotations = annotations;
    }
}
