package tools;

import core.Edge;
import core.Face;
import core.Vertex;
import core.vector.DenseVector;
import core.vector.Vector2d;
import core.vector.Vector3d;
import interfaces.Boundary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Tools {

    public static Vector3d center(Vertex... vertices){
        if(vertices.length == 0) return null;
        Vector3d total = new Vector3d();
        for(Vertex v:vertices){
            total.self_add(v.getPosition());
        }
        return total.self_scale(1d / vertices.length);
    }

    public static double orthog_dist(Vector3d v1, Vector3d v2, Vector3d v3, Vector3d p){
        Vector3d n = (v2.sub(v1)).cross(v3.sub(v1));
        n.self_normalise();
        double dist = n.dot(p.sub(v1));
        return Math.abs(dist);
    }

    public static boolean vectorsAreInPlane(Vector3d... ar){
        if(ar.length <= 3) return true;
        Vector3d normal = ar[2].sub(ar[1]).cross(ar[2].sub(ar[0]));
        for(int i = 3; i < ar.length; i++){
            if(normal.dot(ar[i].sub(ar[0])) != 0){
                return false;
            }
        }
        return true;
    }

    public static boolean vectorsAreInPlane(Vertex... ar){
        if(ar.length <= 3) return true;
        Vector3d normal = ar[2].getPosition().sub(ar[1].getPosition()).cross(ar[2].getPosition().sub(ar[0].getPosition()));
        for(int i = 3; i < ar.length; i++){
            if(normal.dot(ar[i].getPosition().sub(ar[0].getPosition())) != 0){
                return false;
            }
        }
        return true;
    }


    public static boolean contains(Vertex[] ar, Vertex v){
        for(Vertex k:ar){
            if(k.equals(v)) return true;
        }
        return false;
    }

    public static Vector2d circumCenter(Vertex a, Vertex b, Vertex c){

        double a_square = a.getX() * a.getX() + a.getY() * a.getY();
        double b_square = b.getX() * b.getX() + b.getY() * b.getY();
        double c_square = c.getX() * c.getX() + c.getY() * c.getY();

        double d = 2 * (a.getX() * (b.getY()-c.getY()) + b.getX() * (c.getY()-a.getY()) + c.getX() * (a.getY() - b.getY()));
        double x = 1 / d *
                (a_square * (b.getY()-c.getY()) +
                        b_square * (c.getY()-a.getY()) +
                        c_square * (a.getY()-b.getY()));
        double y = 1 / d *
                (a_square * (c.getX()-b.getX()) +
                        b_square * (a.getX()-c.getX()) +
                        c_square * (b.getX()-a.getX()));

        return new Vector2d(x,y);
    }

    public static HashMap<Vertex, Integer> count(Vertex... ar){
        HashMap<Vertex, Integer> map = new HashMap<>();
        for(Vertex v:ar){
            if(map.containsKey(v)){
                map.put(v, map.get(v)+1);
            }else{
                map.put(v,1);
            }
        }
        return map;
    }

    public static HashMap<Vertex, Integer> count(Boundary... ar){
        HashMap<Vertex, Integer> map = new HashMap<>();
        for(Boundary b:ar){
            for(Vertex v:b.getVertices()){
                if(map.containsKey(v)){
                    map.put(v, map.get(v)+1);
                }else{
                    map.put(v,1);
                }
            }
        }
        return map;
    }




    public static void main(String[] args) {
        Vertex v1 = new Vertex(-3,0,0);
        Vertex v2 = new Vertex(1,1,1);
        Vertex v3 = new Vertex(1,0,0);
        Vertex v4 = new Vertex(2,1,1);

        Edge e1 = new Edge(v1,v2);
        Edge e2 = new Edge(v2,v3);
        Edge e3 = new Edge(v3,v4);
        Edge e4 = new Edge(v4,v1);

        Face f = new Face(e1,e2,e3,e4);
        System.out.println(f.volume());
        System.out.println(f.getVertices());

    }

}
