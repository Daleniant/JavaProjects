import java.util.Iterator;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdOut;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private Item[] q; // queue itself
    private int last; // pointer at the end of queue (always at null)
    
    // construct an empty randomized queue
    public RandomizedQueue() {
        q = (Item[]) new Object[1];
        last = 0;
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return last == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return last;
    }
    
    // resize an array that random queue is based on
    private void resize(int n) { 
        Item[] temp = (Item[]) new Object[n];
        for (int i = 0; i < n / 2; i++) 
            temp[i] = q[i];
        q = temp;
    }
    
    // add the item
    public void enqueue(Item item) {
        if (item == null) throw new IllegalArgumentException();
        
        q[last++] = item;
        if (last == q.length) resize(q.length * 2);
    }

    // remove and return a random item
    public Item dequeue() {
        if (isEmpty()) throw new java.util.NoSuchElementException();
        
        // get random index, swap with last element and set new last element to null 
        // (same as delete randomly selected element)
        int i = StdRandom.uniform(last);
        Item item = q[i];
        q[i] = q[--last]; // reduce to non-null item
        q[last] = null; // 'delete' swapped element
        
        // if new active array size is less than quarter - set size to half
        if (last <= q.length / 4) resize(q.length / 2); 
        return item;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (isEmpty()) throw new java.util.NoSuchElementException();
        
        int i = StdRandom.uniform(last);
        return q[i];
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator(){
        return new RandomQueueIterator();
    }
    
    private class RandomQueueIterator implements Iterator<Item> {
        private Item[] qIter;
        private int lastIter;
        
        public RandomQueueIterator() {
            // to avoid changing queue itself - copy to own array
            lastIter = last;
            qIter = (Item[]) new Object[q.length];
            for (int i = 0; i < q.length; i++) {
                qIter[i] = q[i];
            }
        }
        
        public boolean hasNext() {
            return lastIter > 0;
        }
        
        public void remove() {
            throw new UnsupportedOperationException(); // Project specification
        }
        
        public Item next() {
            if (!hasNext()) throw new java.util.NoSuchElementException();
            // Same as regular dequeue
            int i = StdRandom.uniform(lastIter);
            Item temp = qIter[i];
            qIter[i] = qIter[--lastIter];
            qIter[lastIter] = null;
            return temp;
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        int n = 5;
        RandomizedQueue<Integer> queue = new RandomizedQueue<Integer>();
        for (int i = 0; i < n; i++)
            queue.enqueue(i);
        for (int a : queue) {
            for (int b : queue)
                StdOut.print(a + "-" + b + " ");
            StdOut.println();
        }
    }
}