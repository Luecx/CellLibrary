package interfaces;

public interface Container{

    void bindData(double[] data);
    double[] getData();
    default int dataSize(){
        return getData().length;
    }

    void prepare_data();
    void process_data();
}
