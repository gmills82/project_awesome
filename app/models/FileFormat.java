package models;

/**
 An enum for file format constants

 User: justin.podzimek
 Date: 8/2/15
 */
public enum FileFormat {

    EXCEL ("excel");

    /** String representation of the format */
    private String format;

    /**
     Default constructor

     @param format File format
     */
    FileFormat(String format) {
        this.format = format;
    }

    /**
     Returns the file format

     @return File format
     */
    public String getFormat() {
        return format;
    }

    /**
     Returns the enum format for the provided string value

     @param format File format string representation
     @return File format
     */
    public static FileFormat getFormat(String format) {
        for (FileFormat fileFormat : FileFormat.values()) {
            if (format.equalsIgnoreCase(fileFormat.getFormat())) {
                return fileFormat;
            }
        }
        return null;
    }
}
