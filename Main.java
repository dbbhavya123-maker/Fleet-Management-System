import java.util.*;

// 🚚 Truck Class
class Truck {
    int id;
    int capacity;
    boolean available;

    Truck(int id, int capacity) {
        this.id = id;
        this.capacity = capacity;
        this.available = true;
    }

    public String toString() {
        return "Truck ID: " + id + " | Capacity: " + capacity + " | Available: " + available;
    }
}

// 📦 Delivery Class
class Delivery {
    int id;
    int load;

    Delivery(int id, int load) {
        this.id = id;
        this.load = load;
    }
}

// 🌳 AVL Node
class AVLNode {
    Truck truck;
    AVLNode left, right;
    int height;

    AVLNode(Truck t) {
        truck = t;
        height = 1;
    }
}

// 🌳 AVL Tree
class AVLTree {

    int height(AVLNode n) {
        return (n == null) ? 0 : n.height;
    }

    int getBalance(AVLNode n) {
        return (n == null) ? 0 : height(n.left) - height(n.right);
    }

    AVLNode rightRotate(AVLNode y) {
        AVLNode x = y.left;
        AVLNode T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    AVLNode leftRotate(AVLNode x) {
        AVLNode y = x.right;
        AVLNode T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

    AVLNode insert(AVLNode node, Truck t) {
        if (node == null) return new AVLNode(t);

        if (t.id < node.truck.id)
            node.left = insert(node.left, t);
        else if (t.id > node.truck.id)
            node.right = insert(node.right, t);
        else
            return node;

        node.height = 1 + Math.max(height(node.left), height(node.right));

        int balance = getBalance(node);

        // Rotations
        if (balance > 1 && t.id < node.left.truck.id)
            return rightRotate(node);

        if (balance < -1 && t.id > node.right.truck.id)
            return leftRotate(node);

        if (balance > 1 && t.id > node.left.truck.id) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1 && t.id < node.right.truck.id) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    void inorder(AVLNode root) {
        if (root != null) {
            inorder(root.left);
            System.out.println(root.truck);
            inorder(root.right);
        }
    }

    Truck findBestTruck(AVLNode root, int load, Truck best) {
        if (root == null) return best;

        if (root.truck.available && root.truck.capacity >= load) {
            if (best == null || root.truck.capacity < best.capacity) {
                best = root.truck;
            }
        }

        best = findBestTruck(root.left, load, best);
        best = findBestTruck(root.right, load, best);

        return best;
    }
}

// 🧠 Scheduler
class Scheduler {
    AVLTree tree;
    AVLNode root;

    Scheduler() {
        tree = new AVLTree();
        root = null;
    }

    void addTruck(Truck t) {
        root = tree.insert(root, t);
    }

    void assignDelivery(Delivery d) {
        Truck t = tree.findBestTruck(root, d.load,null);

        if (t != null) {
            t.available = false;
            System.out.println("✅ Delivery " + d.id + " assigned to Truck " + t.id);
        } else {
            System.out.println("❌ No suitable truck available for Delivery " + d.id);
        }
    }

    void showTrucks() {
        tree.inorder(root);
    }
    void completeDelivery(int truckId) {
        releaseTruck(root, truckId);
    }

    void releaseTruck(AVLNode node, int id) {
        if (node == null) return;

        if (node.truck.id == id) {
            node.truck.available = true;
            return;
        }

        releaseTruck(node.left, id);
        releaseTruck(node.right, id);
    }
}

// 🚀 Main Simulation
public class Main {
    public static void main(String[] args) {

        Scheduler scheduler = new Scheduler();

        // Add Trucks
        scheduler.addTruck(new Truck(101, 50));
        scheduler.addTruck(new Truck(102, 30));
        scheduler.addTruck(new Truck(103, 70));
        scheduler.addTruck(new Truck(104, 40));

        System.out.println("🚚 Available Trucks:");
        scheduler.showTrucks();

        // Deliveries
        Delivery d1 = new Delivery(1, 35);
        Delivery d2 = new Delivery(2, 60);
        Delivery d3 = new Delivery(3, 20);

        System.out.println("\n📦 Assigning Deliveries:");
        scheduler.assignDelivery(d1);
        scheduler.assignDelivery(d2);
        scheduler.assignDelivery(d3);
        
        System.out.println("\n🔄 Completing Delivery for Truck 101...");
        scheduler.completeDelivery(101);
        
        System.out.println("\n🚚 Updated Truck Status:");
        scheduler.showTrucks();    
    }
}