import java.util.Iterator;
//import edu.princeton.cs.algs4.StdOut;

public class Deque<Item> implements Iterable<Item> {
    private Node first, last;
    private int size;
    
    // construct an empty deque
    public Deque() {
        first = null;
        last = null;
        size = 0;
    }   
    
    private class Node {
        Item item;
        Node next;
        Node prev;
        
        Node (Item itemN, Node nextN, Node previous){
            item = itemN; next = nextN; prev = previous;
        }
    }

    // is the deque empty?
    public boolean isEmpty() {
        return first == null || last == null;
    }

    // return the number of items on the deque
    public int size() {
        return size;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (item == null) throw new IllegalArgumentException();
        
        Node temp = first;
        first = new Node(item, temp, null);
        if (size == 0) last = first; // if deque was only created, first element is also last
        else first.next.prev = first; // otherwise ensure a linear movement backwards
        size++;
    }

    // add the item to the back
    public void addLast(Item item) {
        if (item == null) throw new IllegalArgumentException();
        
        Node temp = last;
        last = new Node(item, null, temp);
        if (size == 0) first = last; // if deque was only created, last element is also first
        else last.prev.next = last; // otherwise ensure a linear movement forward
        size++;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (isEmpty()) throw new java.util.NoSuchElementException();
        
        Item temp = first.item;
        first = first.next;
        // delete reference to previous first element if deque isn't empty
        if (!isEmpty()) first.prev = null; 
        else last = first; // if deque is empty - delete reference that was stored in 'last'
        size--;
        return temp;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (isEmpty()) throw new java.util.NoSuchElementException();
        
        Item temp = last.item;
        last = last.prev;
        // delete reference to previous last element if deque isn't empty
        if (!isEmpty()) last.next = null;
        else first = last; // if deque is empty - delete reference that was stored in 'first'
        size--;
        return temp;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }
    
    // simply move from front to back
    private class DequeIterator implements Iterator<Item> {
        private Node current = first;
        
        public boolean hasNext() {
            return current != null;
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
        public Item next() {
            if (!hasNext()) throw new java.util.NoSuchElementException();
            
            Item temp = current.item;
            current = current.next;
            return temp;
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        Deque<Integer> deque = new Deque<Integer>();
        System.out.println("IsEmpty: " + deque.isEmpty());
        for (int i = 0; i < 5; i++) {
            deque.addFirst(i);
        }
        for (int a : deque) {
            for(int b : deque) {
                System.out.print(a + "-" + b + " ");
            }
            System.out.println();
        }
        for (int i = 0; i < 5; i++) {
            deque.removeLast();
        }
    }
}