package br.com.mycontacts;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import br.com.mycontacts.lista.modelo.Contato;

@SuppressLint("HandlerLeak") public class FormularioHelper {

	private static final Context Formulario = null;
	private EditText editNome, editTelefone, editEmail, editEndereco;
	private RatingBar ratingFavorito;
	private ImageView foto;
	//private Spinner editOperadora;
	public Button editOperadora;
	private Spinner emailtipo;
	private Spinner enderecotipo;
	private Contato contato;
	ArrayAdapter<String> adapterSpinner;
	ArrayAdapter<String> adapterSpinnerTipo;
	ArrayList<String> nomesOperadoras;
	ArrayList<String> tipos;
	//public String operadora;
	public String response, opaux;
	private static String texto;
	
	Thread thread;
//	Handler handler;
	Message msg = new Message();
	
	public FormularioHelper(Formulario formulario) {
		editNome = (EditText) formulario.findViewById(R.id.nome);
		editTelefone = (EditText) formulario.findViewById(R.id.telefone);
		editEmail = (EditText) formulario.findViewById(R.id.email);
		editEndereco = (EditText) formulario.findViewById(R.id.endereco);
		ratingFavorito = (RatingBar) formulario.findViewById(R.id.favorito);
		foto = (ImageView) formulario.findViewById(R.id.foto);
		//editOperadora = (Spinner) formulario.findViewById(R.id.operadora);
		//editOperadora = (TextView) formulario.findViewById(R.id.operadora);
		editOperadora = (Button) formulario.findViewById(R.id.btnOp);
		emailtipo= (Spinner) formulario.findViewById(R.id.emailtipo);
		enderecotipo= (Spinner) formulario.findViewById(R.id.enderecotipo);
     			
	    int layoutSpinner = android.R.layout.simple_spinner_item;
	        
	    //SEMPRE QUE ALTERAR ESSE ARRAY DEVE ALTERAR O ARRAY DO FRAGMENT1.
		//String[] nomesOperadoras ={"Claro", "Nextel", "Oi", "Vivo", "Tim", "Outro"};
		String[] tipos ={"Casa", "Trabalho", "Outro"};
	    
	    //adapterSpinner = new ArrayAdapter<String>(formulario, layoutSpinner, nomesOperadoras);
	    adapterSpinnerTipo = new ArrayAdapter<String>(formulario, layoutSpinner, tipos);
	        
	    //editOperadora.setAdapter(adapterSpinner);
	    emailtipo.setAdapter(adapterSpinnerTipo);
	    enderecotipo.setAdapter(adapterSpinnerTipo);
		
		contato = new Contato();
	}

	public Contato pegaContatoDoFormulario() { 	
		
		contato.setNome(editNome.getText().toString());
		contato.setTelefone(editTelefone.getText().toString());		
		//contato.setOperadora(editOperadora.getSelectedItemId());
		//contato.setOpTelein(editOperadora.getText().toString());
		contato.setEmail(editEmail.getText().toString());
		contato.setTipoemail((int) emailtipo.getSelectedItemId());
		contato.setEndereco(editEndereco.getText().toString());
		contato.setTipoendereco((int) enderecotipo.getSelectedItemId());
		contato.setFavorito(Double.valueOf(ratingFavorito.getRating()));

		 /*handler = new Handler(){
			public void handleMessage(Message op) {
				texto = (String)op.obj;								
				editOperadora.setText(texto);
				contato.setOpTelein(texto);
				Log.i("CONTATO 1: ", contato.getOpTelein());
			}
		};
		
		thread=new Thread(new MyThread(handler));
		thread.start();*/
		//testeHandler();
		
		contato.setOpTelein(editOperadora.getText().toString());
		
		Log.i("editOperadora", "é: "+editOperadora.getText().toString());
		Log.i("CONTATO2", "= "+contato.getOpTelein());
		
		return contato;
	}
	
	/*public void testeHandler(){
        final Handler handler = new Handler();
        final String resultado;

        new Thread(){

			@Override
            public void run() {
                try {
                    // aqui faz o processo dentro da Thread secundaria, fora da Thread Main e busca o retorno de um WebService
                   //resultado = artigoDao.consultarSituacao();
                	resultado = "TESTE";
                    handler.post(new Runnable() {
                        public void run() {
                            // Aqui dentro do Handler atualiza a view com o retorno, dentro da Thread Main
                            //TextView textViewSituacao  = (TextView) findViewById(R.id.textViewSituacao);
                        	editOperadora.setText("ATUALIZADO");
                        	//Toast.makeText(SimulacaoVendaActivity.this, "Artigos Sincronizados com Sucesso.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (final Exception e) {
                    e.getMessage();
                }
            }
        }.start();    
        Log.i("TESTE: ", "= "+resultado);
	}
	*/
	
	/*class MyThread implements Runnable {
		public MyThread(Handler handler) {
				//String URL = "aqui será a url";
                //SynchronousHttpConnection httpConnection = new SynchronousHttpConnection();
		}
		@Override
		public void run() {
			try {   
				//response = httpConnection.post(URL);
				msg.obj = "xxx";
				handler.sendMessage(msg);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} 				
		}
	}	*/
	/*public void DescobrirOperadoraTelein(){
		new Thread() {
			@Override
            public void run() {
                super.run();
        		//String URL = "aqui será url";
                SynchronousHttpConnection httpConnection = new SynchronousHttpConnection();
        		try {   
        			//response = httpConnection.post(URL);
        			response = "testetestando";  
        			Message msg = Message.obtain();
        			handler.sendMessage(msg);
        		    //TesteAux(response);        			
        		    //Log.i("X1", "Resposta: " + response);
        		} catch (IllegalStateException e) {
                  e.printStackTrace();
        		} catch (IOException e) {
                  e.printStackTrace();
        		}        	
			}
		}.start();
	}

	*/
	
	public void colocaContatoNoFormulario(Contato contatoMostrar, String mostrarOuAlterar) {
		contato = contatoMostrar;
		editNome.setText(contatoMostrar.getNome());
		editTelefone.setText(contatoMostrar.getTelefone());
		editEmail.setText(contatoMostrar.getEmail());
		emailtipo.setSelection(contatoMostrar.getTipoemail());
		editEndereco.setText(contatoMostrar.getEndereco());
		enderecotipo.setSelection(contatoMostrar.getTipoendereco());
		ratingFavorito.setRating(contatoMostrar.getFavorito().floatValue());
		//editOperadora.setSelection(contatoMostrar.getOperadora());
		editOperadora.setText(contatoMostrar.getOpTelein());
		
		if (contato.getFoto() != null) {
			carregaImagem(contatoMostrar.getFoto());
		}
		
		if (mostrarOuAlterar == "contatoMostrar") {
			editNome.setEnabled(false);
			editTelefone.setEnabled(false);
			editEmail.setEnabled(false);
			emailtipo.setEnabled(false);
			editEndereco.setEnabled(false);	
			enderecotipo.setEnabled(false);
			ratingFavorito.setEnabled(false);
			foto.setEnabled(false);	
			editOperadora.setEnabled(false);
		}
	}
	
	public ImageView getFoto() {
		return foto;
	}
	public void carregaImagem(String caminhoArquivo) {
		contato.setFoto(caminhoArquivo);
		//Fazer imagem que foi tirada
		Bitmap imagem = BitmapFactory.decodeFile(caminhoArquivo);
		/*Reduzir imagem*/
		Bitmap imagemReduzida = Bitmap.createScaledBitmap(imagem, 100, 100, true);
		//Carregar imagem que foi tirada
		foto.setImageBitmap(imagemReduzida);
	}
}
