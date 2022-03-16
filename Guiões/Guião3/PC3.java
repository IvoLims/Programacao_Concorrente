import java.util.HashMap;
import java.util.concurrent.Lock;
import java.util.concurrent.ReentrantLock;
import java.util.Collections;

/*1. Modifique o exercício do banco de modo a que, em vez de existir um número fixo de contas,
seja possível criar contas e fechar contas, como na seguinte interface:

interface Bank {
int createAccount(int initialBalance);
int closeAccount(int id) throws InvalidAccount;
void deposit(int id, int val) throws InvalidAccount;
void withdraw(int id, int val) throws InvalidAccount, NotEnoughFunds;
void transfer(int from, int to, int amount) throws InvalidAccount, NotEnoughFunds;
int totalBalance(int accounts[]) throws InvalidAccount;
}

em que criar uma conta devolve um identificador de conta, para ser usado em outras operações e fechar uma conta 
devolve o saldo desta; deverá ainda ser possível obter a soma do saldo de um conjunto de contas. Algumas operações 
deverão poder lançar exceções se o identificador de conta não existir ou não houver saldo suficiente.
Apesar de permitir concorrência, garanta que os resultados sejam equivalentes a ter acontecido uma operação de cada 
vez. Por exemplo, ao somar os saldos de um conjunto de contas, não permita que sejam usados montantes a meio de uma 
transferência (e.g., depois de retirar da conta origem e antes de somar à conta destino).*/

public class NotEnoughFunds extends Exception{
	public NotEnoughFundsException(){super();}
	public NotEnoughFundsException(String msg){
		super("Sem Fundos Suficientes "+msg);
	}
}

public class InvalidAccount extends Exception{
	public InvalidAccountException(){super();}
	public InvalidAccountException(String msg){
		super("Conta Invalida "+msg);
	}
}

class Bank{
	private static class Account{
		private int balance;
		public synchronized int balance(){
			return balance;
		}
		public void deposit(int val){
			balance += val;
		}
		public void witdraw(int val) throws NotEnoughFunds{
			if (balance < val) throw new NotEnoughFunds();
			balance -= val;
		}
		Lock l = new ReentrantLock();
	}
	private HashMap<Integer,Account> accounts = new HashMap<>();
	private Lock l = new ReentrantLock();
	private int lastId = 0;
    public int createAccount(int initialBalance){
    	//Nenhum destes são dependentes logo podem estar fora
    	Account c = new Account();
    	l.lock();
    	try{
    		accounts.put(lastId++,c.deposit(initialBalance)); //Colocar na hashMap
        }finally{
        	l.unlock();
        }
        return lastId - 1;
    }
    public int closeAccount(int id) throws InvalidAccount{
    	Account c;
    	l.lock();
    	try{
    		Account c = accounts.get(id);
    	    if(c == null) throw new InvalidAccount();
    	    c.remove(id);
    	    c.l.lock();
    	}finally{
    		l.unlock();
    	}try{
    	    return c.balance();
    	}finally{
    		c.l.unlock();
    	}
    }
	public void deposit(int id,int val) throws InvalidAccount{
		Account c;
		l.lock(); //Lock do Banco
		try{
			c = accounts.get(id);
		    if(c == null) throw new InvalidAccount();
		    c.l.lock();
		}finally{
			l.unlock();
		}
		try{
			c.deposit(val);
		}finally{
			c.l.unlock();
		}
	}
	public void witdraw(int id, int val) throws InvalidAccount, NotEnoughFunds{
		Account c;
		l.lock();
		try{
			c = accounts.get(id);
			if(c == null) throw new InvalidAccount();
			c.l.lock(); //Lock Conta
		} finally{
			l.unlock();
		}
		try{
			c.witdraw(val);
		} finally{
			c.l.unlock();
		}
	}
	public int totalBalance(int accs[]) throws InvalidAccount{
		accs.clone();
		Arrays.sort(acs);
		int total = 0;
		Account[] a = new Account[accs.length];
		l.lock();
		try{
			for(int i = 0; i < accs.length; i++){
			    c = accounts.get(accs[i]);
			    if(c == null) throw new InvalidAccount(); 
                a[i] = c;
            }
            for(Account c : a) c.l.lock();
		}finally{
			l.unlock();
		}try{
			for(Account c : a){
				int val = c.balance();
				total += val;
			}
		}finally{
			c.l.unlock();
		}
		return total;
	}
	public void transfer(int from, int to, int val) throws InvalidAccount, NotEnoughFundsException{
		if(from == to) return;
		if(!this.accounts.containsKey(from) || !this.accounts.containsKey(to)) throw new InvalidAccount();
		Account cfrom, cto, a1, a2;
		l.lock();
		try{
		cfrom = get(from);
		cto = get(to);	
		if(from < to){
			a1 = cfrom;
			a2 = cto;
		}else{
			a1 = cto;
			a2 = from;
		}
		a1.l.lock();
		a2.l.lock();
		}finally{
			l.unlock();
		}
		try{
			cfrom.witdraw(val);
		    cto.deposit(val);
		}finally{
			a1.l.unlock();
			a2.l.unlock();
		}
	}
	int accountBalance(int id) throws InvalidAccount {
        this.lock.lock();
        Account a;

        try {
            a = this.accounts.get(id);

            if (a == null) throw new InvalidAccount();

            a.lock.lock();
        } finally {
            this.lock.unlock();
        }

        try {
            return a.balance();
        } finally {
            a.lock.unlock();
        }
    }
}

class Depositer extends Thread{
	final int iterations;
	final Bank b;
	Depositor(int iterations, Bank b){ 
		this.iterations = iterations;
	    this.b = b;
	}
	public void run(){
		for(int i = 0; i<iterations; ++i){
			b.deposit(i % b.accounts.length,1);
		}
	}
}

public class Main {
    public static void main(String args[]) {
            try {
                int accounts[] = new int[10];
                int n = 10;
                Bank b = new Bank();

                for (int i = 0; i < n; i++) accounts[i] = b.createAccount(i+1);
                for (int i = 0; i < n; i++) b.deposit(accounts[i], 100*(i+1));

                printAccountBalances(accounts, b, n);

                System.out.println("Closed account 5 with $" + b.closeAccount(5) + ".");

                b.transfer(9, 0, 300);
                int newList[] = {0,1,2,3,4,6,7,8,9};
                System.out.println(b.totalBalance(newList));
            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    public static void printAccountBalances(int accounts[], Bank b, int n) throws InvalidAccount, NotEnoughFunds {
        for (int i = 0; i < n; i++) {
            try {
                System.out.println("Account " + i + ": " + b.accountBalance(accounts[i]));
            } catch (InvalidAccount ia) {
                System.out.println("Account "+ i + " does not exist.");
            }
        }
    }
}
