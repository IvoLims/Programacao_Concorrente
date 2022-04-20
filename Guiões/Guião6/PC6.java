import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.HashMap;
import java.util.Map;

/* 1. Reimplemente a classe BoundedBuffer, de modo a evitar acordar threads desnecessariamente, 
distinguindo as situações de bloqueio pelo array estar vazio e cheio. ´*/

class BoundedBuffer{
    private int[] buff;
    private int nelems = 0;//número de elementos no buffer
    private int iget = 0; //último indice do array onde se encontra o último elemento do mesmo
    private int iput = 0; //primeiro índice do array livre
    
    Lock l = new ReentrantLock();
    Condition notEmpty = l.newCondition();
    Condition notFull = l.newCondition();

    public BoundedBuffer(int N) {
       buff = new int[N];
    }

    public int get() throws InterruptedException { 
        l.lock();
        try{
            while(nelems == 0)
                notEmpty.await();
            int x;
            x = buff[iget];
            iget = (iget + 1) % buff.length;
            notFull.signal();
            nelems -=1;
            return x;
        }
        finally{
           l.unlock();
        }
    }

    public void put(int x) throws InterruptedException {
        l.lock();
        try{
            while(nelems==buff.length)
                notFull.await();
            buff[iput]=x;
            iput = (iput + 1) % buff.length;
            nelems += 1;
            notEmpty.signal();
        }finally{
            l.unlock();
        }
    }
}

class Main{
    public static void main(String [] args){
        BoundedBuffer b = new BoundedBuffer(20); 
        new Thread(() -> {
            try{
                for(int i= 1;; ++i){
                    System.out.println("Vou fazer put de "+ i);
                    b.put(i);
                    System.out.println("Fizemos put de " + i);
                    Thread.sleep(200);
                }
            } catch(InterruptedException e){}
        }).start();

        new Thread(() -> {
            try{
                for(int i= 1; ;++i){
                    System.out.println("Vou fazer get de "+ i);
                    int j = b.get();
                    System.out.println("O get returnou " + j);
                    Thread.sleep(2000);
                }
            }catch(InterruptedException e){}            
        }).start();
    }
}

/* 2. Implemente uma classe Warehouse para permitir a gestão de um armazem acedido concorrentemente. 
Deverão ser disponibilizados os métodos:

• supply(String item, int quantity) – abastecer o armazem com uma dada
quantidade de um item;

• consume(String[] items) – obter do armazem um conjunto de itens, bloqueando enquanto tal não for possível. */

class WareHouse{
    Map<String, Item> armazem = new HashMap<>();
    Lock l = new ReentrantLock();

    private class Item{
        int quant = 0;
        Condition cond = l.newCondition();
    }
   
    private Item get(String s){
        Item item = armazem.get(s);
        if (item==null){
            item = new Item();
            armazem.put(s,item);
        }
        return item;
    }

    public void supply(String item, int quantity){
        l.lock();
        try{
            Item c = get(item);
            c.quant += quantity;
            c.cond.signalAll();
        }finally{
            l.unlock();
        }
    }

    public void consume(String[] items) throws InterruptedException{
        l.lock();
        try{
            for (String s: items){
                Item item = get(s);
                while(item.quant == 0)
                    item.cond.await();
                item.quant -=1;
            }
        }
        finally{
           l.unlock();
        }
    }
}

/* 3. Implemente a classe RWLock com os métodos readLock(), readUnlock(), writeLock()
e writeUnlock(), de modo a permitir o acesso simultâneo de múltiplos leitores a uma
dada região crítica, ou em alternativa, o acesso de um único escritor. Procure evitar starvation. */
