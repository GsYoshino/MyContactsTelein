package br.com.mycontacts;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import br.com.mycontacts.lista.dao.ContatoDAO;
import br.com.mycontacts.lista.modelo.Contato;


@SuppressLint("NewApi") public class Formulario extends Activity{
	
	private FormularioHelper helper;
	private String caminhoArquivo;
	
	private Contato contatoMostrar;
	private Contato contatoAlterar;
	
	private Contato contato;
	private ContatoDAO dao;
	private ImageButton botao;
	private Button btnOp;
	private boolean i;
	private MainActivity MainActtivity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView � o m�todo mais importante da activity.
		//Ele vincula a Activity Formulario com o layout formulario.xml
		setContentView(R.layout.formulario);
		
		//Para bot�o de voltar no logo
		android.app.ActionBar ab=getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
        
		Intent intent = getIntent();
		
		contatoMostrar = (Contato) intent.getSerializableExtra("contatoMostrar");
		contatoAlterar = (Contato) intent.getSerializableExtra("contatoAlterar");
		
		//Extrair o codigo em uma classe separada, para evitar que a Activity cres�a demais.
		helper = new FormularioHelper(this);
		
		botao = (ImageButton) findViewById(R.id.botao);

		botao.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				Contato contato = helper.pegaContatoDoFormulario();
				//especialista para salvar no Sqlite
				ContatoDAO dao = new ContatoDAO(Formulario.this);
				
				if (contatoAlterar == null && contatoMostrar == null){					
					dao.salva(contato);		
				} else if (contatoAlterar != null) {
					//contato.setId(contato.getId());
					dao.alterar(contato);
				}
				dao.close();			
				
				finish(); //Bot�o voltar automatico
			}
		});		
		
		btnOp = (Button) findViewById(R.id.btnOp);
		btnOp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final Handler handler = new Handler();
			        new Thread(){
						@Override
			            public void run() {
			                try {
			                	Class_Operadora classe_op = new Class_Operadora();
			                	final String NomeOperadora;
			                	String response;
			                  
			                	//String URL = "http://consultaoperadora1.telein.com.br/sistema/consulta_resumida.php?numero=11962301830&chave=74b451b7a6ef79a57085";
			                	//SynchronousHttpConnection httpConnection = new SynchronousHttpConnection();
			                	//response = httpConnection.post(URL);
			                	response = "21#962301830";
			                	
			                	NomeOperadora = classe_op.NomearOperadora(response);
			                	
			                	Log.i("RESPONSE ", ": "+NomeOperadora);
			                    handler.post(new Runnable() {
			                        public void run() {
			                            // Aqui dentro do Handler atualiza a view com o retorno, dentro da Thread Main
			                            //TextView textViewSituacao  = (TextView) findViewById(R.id.textViewSituacao);
			                        	btnOp.setText(NomeOperadora);
			                        	//Toast.makeText(SimulacaoVendaActivity.this, "Artigos Sincronizados com Sucesso.", Toast.LENGTH_SHORT).show();
			                        }
			                    });
			                } catch (final Exception e) {
			                    e.getMessage();
			                }
			            }
			        }.start();    			
			}
		});
			
		
		ImageView foto = helper.getFoto();
		foto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent irParaCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				
				caminhoArquivo = Environment.getExternalStorageDirectory().toString()
						+"/"+System.currentTimeMillis()+".png";
				
				/*Criando arquivo, deve ser um FILE*/
				File arquivo = new File(caminhoArquivo);
				
				/*S� que no Android a identifica��o de caminho tem que ser especificado em uma URI*/
				Uri localImagem = Uri.fromFile(arquivo);
				
				/*MediaStore.EXTRA_OUTPUT apelido conhecido pelo android para a Intent de camera saber o local da imagem*/
				irParaCamera.putExtra(MediaStore.EXTRA_OUTPUT, localImagem);
				
				/*O codigo do resultado para tentativa de tirar foto � 123*/
				startActivityForResult(irParaCamera, 123);
			}
		});
	}
	@Override
	protected void onResume() {
		super.onResume();
		if(contatoMostrar != null){
			botao.setVisibility(View.INVISIBLE);				
			helper.colocaContatoNoFormulario(contatoMostrar, "contatoMostrar");
		} else if (contatoAlterar != null) {
			helper.colocaContatoNoFormulario(contatoAlterar, "contatoAlterar");
		}
	}
			
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		/*Para saber se estamos voltando da chamada da camera comparamos o requestCode == 123*/
		if (requestCode == 123) {
			if (resultCode == Activity.RESULT_OK) {

				//ok para foto
				helper.carregaImagem(caminhoArquivo);
			} else {
				//cancelou foto
				caminhoArquivo = null;
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater(); //getMenuInflater retorna o menu inflate
		if(contatoMostrar != null){
			//Visualizar
			inflater.inflate(R.menu.viewer_contato, menu); //metodo inflate, devemos passar o xml que queremos inflar
		}
		else{
			inflater.inflate(R.menu.formularios, menu); //Editar formulario
		}
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem itemSelect) {
		int itemClicado = itemSelect.getItemId();//descobrir qual o item clicado
		
		switch (itemClicado) {
			case android.R.id.home:
				Intent irHome=new Intent(this,MainActivity.class); 
				startActivity(irHome);
				break;
			case R.id.add_campo:
				Toast.makeText(Formulario.this, "Adicionar campo", Toast.LENGTH_SHORT).show();
				break;
			case R.id.deletar:
				AlertDialog ad = new AlertDialog.Builder(this)
	            .create();
			    ad.setTitle("Realmente excluir?");
			    ad.setMessage("Deseja realmente excluir o contato selecionado?");
			    ad.setButton("Sim", new DialogInterface.OnClickListener() { 
			    	public void onClick(DialogInterface arg0, int arg1) { 
			    		ContatoDAO dao = new ContatoDAO(Formulario.this); //Chamada o SQLITE
			        	dao.deletar(contatoMostrar);
						dao.close();
						Intent irLista = new Intent(Formulario.this, MainActivity.class);
			            Formulario.this.startActivity(irLista); 
			    	} 
			    }); 
			    ad.setButton2("N�o", new DialogInterface.OnClickListener() { 
			    	public void onClick(DialogInterface arg0, int arg1) { 
			    	} 
			    });
			    ad.show();
				break;
			case R.id.editar:
				Intent irParaFormulario = new Intent(this, Formulario.class);
				contato = helper.pegaContatoDoFormulario();
				
				irParaFormulario.putExtra("contatoAlterar", contato);
				startActivity(irParaFormulario);		

				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(itemSelect);
	}
}
