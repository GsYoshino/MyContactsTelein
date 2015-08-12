package br.com.mycontacts;

public class Class_Operadora {

	
	public String NomearOperadora(String request){
		//VERIFICAR SE NÃO TEM DDD COM 3 DIGITOS.
		String ddd = request.substring(0, 2);
		
		switch (ddd) {
		case "21":
			ddd = "Claro";
			
			break;
		default:
			
			break;
		}
		
		
		return ddd;
	}
}
