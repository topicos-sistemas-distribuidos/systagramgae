package br.ufc.great.tsd.util;

public class MyEnconder {

	public String encode(String valor) {
		return valor;
	}

	public boolean matches(String password, String senhaCriptografada) {
		boolean check=false;
		
		if (password.equals(senhaCriptografada)) {
			check = true;
		}else {
			check = false;
		}
		
		return check;
	}

}
