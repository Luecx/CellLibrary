package tools;

import core.Edge;
import core.Face;
import core.Vertex;
import core.Volume;
import core.vector.Vector2d;
import structs.Mesh;
import structs.Triangle;

import java.util.ArrayList;

public class Generator {

    /**
     * first index = x
     * second index = y
     *
     * @param nodes
     * @return
     */
    public static <T extends Mesh> T connect_rectangular_nodes(T mesh, Vertex[][] nodes, boolean loopX) {
        ArrayList<Face> triangles = new ArrayList<>();
        ArrayList<Edge> nodes_list = new ArrayList<>();

        Edge[][][] edges = new Edge[nodes.length - 1 + (loopX ? 1:0)][nodes[0].length - 1][];

        //generating edges
        for (int i = 0; i < nodes.length - 1 + (loopX ? 1:0); i++) {
            for (int n = 0; n < nodes[0].length - 1; n++) {
                if ((n + i) % 2 == 0) {
                    edges[i][n] = new Edge[]{
                            mesh.new_edge(nodes[i][n], nodes[(i+1) % nodes.length][n+1]),
                            mesh.new_edge(nodes[(i+1) % nodes.length][n+1], nodes[i][n+1]),
                            mesh.new_edge(nodes[i][n+1], nodes[i][n]),
                            mesh.new_edge(nodes[i][n], nodes[(i+1) % nodes.length][n]),
                            mesh.new_edge(nodes[(i+1) % nodes.length][n], nodes[(i+1) % nodes.length][n+1]),
                            mesh.new_edge(nodes[(i+1) % nodes.length][n+1], nodes[i][n])};
                }else{
                    edges[i][n] = new Edge[]{
                            mesh.new_edge(nodes[(i+1) % nodes.length][n], nodes[i][n+1]),
                            mesh.new_edge(nodes[(i+1) % nodes.length][n+1], nodes[i][n+1]),
                            mesh.new_edge(nodes[i][n+1], nodes[i][n]),
                            mesh.new_edge(nodes[i][n], nodes[(i+1) % nodes.length][n]),
                            mesh.new_edge(nodes[(i+1) % nodes.length][n], nodes[(i+1) % nodes.length][n+1]),
                            mesh.new_edge(nodes[i][n+1], nodes[(i+1) % nodes.length][n])};
                }

            }
        }

        //linking edges
        for (int i = 0; i < nodes.length - 1+ (loopX ? 1:0); i++) {
            for (int n = 0; n < nodes[0].length - 1; n++) {
                edges[i][n][0].link(edges[i][n][5]);
                if(n > 0){
                    edges[i][n][3].link(edges[i][n-1][1]);
                }
                if(i > 0){
                    edges[i][n][2].link(edges[i-1][n][4]);
                }
            }
        }

        //generating faces
        for (int i = 0; i < nodes.length - 1 + (loopX ? 1:0); i++) {
            for (int n = 0; n < nodes[0].length - 1; n++) {
                if ((n + i) % 2 == 0) {
                    triangles.add(mesh.new_face(edges[i][n][0], edges[i][n][1], edges[i][n][2]));
                    triangles.add(mesh.new_face(edges[i][n][3], edges[i][n][4], edges[i][n][5]));
                }else{
                    triangles.add(mesh.new_face(edges[i][n][0], edges[i][n][2], edges[i][n][3]));
                    triangles.add(mesh.new_face(edges[i][n][1], edges[i][n][5], edges[i][n][4]));
                }
            }
        }



        T m = (T) mesh.new_mesh();
        for(Face t:triangles){
            m.addFace(t);
        }
        return m;
    }

    public static <T extends Mesh> T arc_mesh(T mesh, double lever_length, double r_min, double r_max, double degrees, int subd_l, int subd_w) {
        return Generator.connect_rectangular_nodes(mesh, arc_mesh_nodes(mesh, lever_length, r_min, r_max, degrees, subd_l, subd_w),false);
    }

    public static <T extends Mesh> T rectangle_mesh(T mesh, double w, double h, int subd_w, int subd_h) {

        return Generator.connect_rectangular_nodes(mesh, rectangle_mesh_nodes(mesh,w,h,subd_w,subd_h),false);
    }

    public static <T extends Mesh> T rectangle_hole_mesh_connected(T mesh, double w, double r, int subd_a, int subd_r){
        subd_a =  ((subd_a / 8)) * 8;
        Vertex[][] nodes = new Vertex[8 * subd_a][subd_r + 1];

        for(int k = 0; k < subd_a * 8 - 0.5; k++){
            double angle = Math.PI * 2 * k / (subd_a * 8);
            double inner = r;
            double outer = distanceInQuad(w, angle);
            for(int n = 0; n < subd_r + 0.5; n++){
                double rad = inner + ((double)n / subd_r) * (outer -inner);
                Vertex node = mesh.new_vertex(Math.cos(angle) * rad, Math.sin(angle) * rad, 0);
                nodes[k][n] = node;
            }
        }
        return connect_rectangular_nodes(mesh, nodes, true);
    }

    public static <T extends Mesh> T rectangle_hole_mesh(T mesh, double w, double r, int subd_a, int subd_r){
        subd_a =  ((subd_a / 8)) * 8;
        Vertex[][] nodes = new Vertex[8 * subd_a][subd_r + 1];

        for(int k = 0; k < subd_a * 8 - 0.5; k++){
            double angle = Math.PI * 2 * k / (subd_a * 8);
            double inner = r;
            double outer = distanceInQuad(w, angle);
            for(int n = 0; n < subd_r + 0.5; n++){
                double rad = inner + ((double)n / subd_r) * (outer -inner);
                Vertex node = mesh.new_vertex(Math.cos(angle) * rad, Math.sin(angle) * rad,0);
                nodes[k][n] = node;
            }
        }
        return connect_rectangular_nodes(mesh, nodes, false);
    }

    public static Vertex[][] arc_mesh_nodes(Mesh mesh, double lever_length, double r_min, double r_max, double degrees, int subd_l, int subd_w) {
        Vertex[][] nodes = new Vertex[subd_l + 1][subd_w + 1];
        for (int i = 0; i < subd_l + 1; i++) {
            for (int n = 0; n < subd_w + 1; n++) {
                double radius = n / (double) subd_w * (r_max - r_min) + r_min;
                double x = i / (double) subd_l;
                Vector2d arc = arc_function(lever_length, radius, r_max, degrees, x);
                nodes[i][n] = mesh.new_vertex(arc.getX(), arc.getY(),0);
            }
        }
        return nodes;
    }

    public static Vertex[][] rectangle_mesh_nodes(Mesh mesh, double w, double h, int subd_w, int subd_h) {

        Vertex[][] nodes = new Vertex[subd_w + 1][subd_h + 1];

        for (int n = 0; n < subd_h + 1; n++) {
            for (int i = 0; i < subd_w + 1; i++) {
                Vertex node = mesh.new_vertex((double) w / (subd_w) * i, (double) h / (subd_h) * n,0);
                nodes[i][n] = node;

            }
        }

        return nodes;
    }


    public static Vector2d arc_function(double lever_length, double radius, double ref_radius, double degrees, double x) {
        double arc_l = ref_radius * Math.PI * degrees / 180;
        double total_l = lever_length * 2 + arc_l;
        if (x < lever_length / total_l) {
            return new Vector2d(-radius, -(lever_length - x * total_l));
        } else if (x < 1 - lever_length / total_l) {
            double angle = (x - lever_length / total_l) * total_l / arc_l * degrees;
            return new Vector2d(radius * -Math.cos(Math.toRadians(angle)), radius * Math.sin(Math.toRadians(angle)));
        } else {
            Vector2d start = new Vector2d(radius * -Math.cos(Math.toRadians(degrees)), radius * Math.sin(Math.toRadians(degrees)));
            Vector2d direction = new Vector2d(Math.sin(Math.toRadians(degrees)), Math.cos(Math.toRadians(degrees)));
            double part = (x - (1 - lever_length / total_l)) * total_l / lever_length;
            return new Vector2d(start.add(direction.self_scale(part * lever_length)));

        }
    }

    /**
     * calculates the distance from the center of a quad with side length a given the angle
     *
     * ___y___
     * |  |  |
     * |  |__|x
     * |     |
     * |_____|
     * @param angle
     * @return
     */
    public static double distanceInQuad(double a, double angle) {
        angle %= (Math.PI / 2);
        if(angle > Math.PI / 4){
            angle = Math.PI / 2 - angle;
        }
        return a/2 / Math.cos(angle);
    }




}
