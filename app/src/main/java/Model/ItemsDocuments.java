package Model;

public class ItemsDocuments {
    int id;
    int idDocument;
    int idGood;
    String titleGood = "";
    String barcodeGood;
//    boolean isCorrectBarcode = true;
    int quantity;

    public ItemsDocuments(int idDocument, int idGood, int quantity) {
        this.idDocument = idDocument;
        this.idGood = idGood;
        this.quantity = quantity;
    }

    public ItemsDocuments(int idGood, int quantity) {
        this.idGood = idGood;
        this.quantity = quantity;
    }


    public ItemsDocuments(String titleGood, String barcodeGood, int quantity) {
        this.titleGood = titleGood;
        this.barcodeGood = barcodeGood;
        this.quantity = quantity;
    }

    public ItemsDocuments() {

    }

//    public boolean isCorrectBarcode() {
//        return isCorrectBarcode;
//    }
//
//    public void setCorrectBarcode(boolean correctBarcode) {
//        isCorrectBarcode = correctBarcode;
//    }

    public String getTitleGood() {
        return titleGood;
    }

    public void setTitleGood(String titleGood) {
        this.titleGood = titleGood;
    }

    public String getBarcodeGood() {
        return barcodeGood;
    }

    public void setBarcodeGood(String barcodeGood) {
        this.barcodeGood = barcodeGood;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(int idDocument) {
        this.idDocument = idDocument;
    }

    public int getIdGood() {
        return idGood;
    }

    public void setIdGood(int idGood) {
        this.idGood = idGood;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

