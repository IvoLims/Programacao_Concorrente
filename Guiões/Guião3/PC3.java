import java.util.HashMap;
import java.util.concurrent.locks;

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
	HashMap<Integer,Account> accounts = new HashMap<>();
	Lock l = new ReentrantLock();
	int lastId = 0;
    public int createAccount(int initialBalance){
    	//Nenhum destes são dependentes logo podem estar fora
    	Account c = new Account();
    	c.deposit(initialBalance);
    	l.lock();
    	try{
    	    lastId+=1;
    	    int id = lastId;
    	    accounts.put(id,c); //Colocar na hashMap
    	    return id;
        }finally{
        	l.unlock();
        }
    }
    public int closeAccount(int id) throws InvalidAccount{
    	l.lock();
    	Account c = accounts.get(id);
    	if(c == null) throw new InvalidAccount();
    	c.remove(id)
    	l.unlock();
    }
	public void deposit(int id,int val) throws InvalidAccount{
		l.lock();
		try{
			Account c = accounts.get(id);
		    if(c == null) throw new InvalidAccount();
		    c.deposit(val);
		}finally{
			l.unlock();
		}
	}
	public void witdraw(int id, int val) throws InvalidAccount, NotEnoughFunds{
		l.lock();
		try{
			Account c = accounts.get(id);
			if(c == null) throw new InvalidAccount();
			c.witdraw(val);
		} finally{
			l.unlock();
		}
	}
	public int totalBalance(int accounts[]) throws InvalidAccount{
		int total = 0;
		for(int id : accounts){
			total += get(id).balance();
		}
		return total;
	}
	public void transfer(int from, int to, int val) throws InvalidAccount, NotEnoughFundsException{
		if(from == to) return;
		Account cfrom = get(from);
		Account cto = get(to);
		if(from < to){
			a1 = cfrom;
			a2 = cto;
		} else{
			a1 = cto;
			a2 = from;
		}
		synchronized(a1){
			synchronized(a2){
				cfrom.witdraw(val);
		        cto.deposit(val);
			}
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

class Main{
	public static void main(String[] args) throws InterruptedException, InvalidAccount{
		final int N = 	Integer.parseInt(args[0]);
		final int NC = 	Integer.parseInt(args[1]);
		final int I = Integer.parseInt(args[2]);

		Bank b = new Bank(NC);
		Thread[] a = new Thread[N];
		int todasContas = new int[NC];

		for(int i = 0; i<NC; ++i){
			todasContas[i] = i;
		}

		/*
		for(int i = 0; i<NC; ++i){
			b.deposit(i, 1000);
		}
		*/

		for(int i = 0; i<N; ++i){
			a[i] = new Depositor(I,b);
			a[i].start();
			a[i].join();
		}
		System.out.println(b.totalBalance(todasContas));
	}
}
