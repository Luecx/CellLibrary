package interfaces;

import core.vector.Vector3d;
import tools.Tools;

import java.util.ArrayList;


public abstract class Cell<C extends Boundary, T extends Cell<C,T,S>, S extends Cell> extends Boundary<T,S> {



    protected C[] boundaries;

    public Cell() {
        super();
    }

    public ArrayList<T> getNeighbors(){
        ArrayList<T> ar = new ArrayList<>();
        for(C c:boundaries){
            if(c.hasLinkedBoundary()){
                ar.add((T) c.getLinkedBoundary().getCell());
            }
        }
        return ar;
    }

    public double volume(){
        return this.integral();
    }
    public abstract boolean inside(Vector3d vec);

    public Vector3d center(){
        return Tools.center(vertices);
    }

    public C[] getBoundaries() {
        return boundaries;
    }
    public void setBoundaries(C[] boundaries) {
        this.boundaries = boundaries;
    }


}
