package EmailApp.reader;

import java.util.Set;

import EmailApp.Patient;

abstract public class MyDataReader<T> {
	abstract Set<Patient> read(T dataSource);
}
