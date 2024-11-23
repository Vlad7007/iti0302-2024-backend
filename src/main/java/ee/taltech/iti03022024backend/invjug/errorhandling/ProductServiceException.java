package ee.taltech.iti03022024backend.invjug.errorhandling;

public class ProductServiceException extends Exception {
    public ProductServiceException(String message) {
        super(message);
    }
}