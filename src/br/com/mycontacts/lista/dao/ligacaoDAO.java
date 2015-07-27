package br.com.mycontacts.lista.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;
import br.com.mycontacts.lista.modelo.Contato;
import br.com.mycontacts.lista.modelo.Ligacao;


public class ligacaoDAO extends SQLiteOpenHelper {
	private static final String DATABASE = "Lista de Contatos 2";
	private static final int VERSAO = 1;

	public ligacaoDAO(Context context) {
		super(context, DATABASE, null, VERSAO);
	}

	public void salva(Contato registroChamada)  { //pegar os parametros do contato e n�o da ligaca��o
		ContentValues values = new ContentValues();
		//Contato dados = new Contato();
		values.put("idContato", registroChamada.getId());
		values.put("nome", registroChamada.getNome());
		values.put("telefone", registroChamada.getTelefone());
		values.put("foto", registroChamada.getFoto());
		Log.i("SCRIPT", "SALVOU ESSSA BIROSCA 1"+registroChamada.getId());
		//values.put("hora", dados.getHoraLigacao());
		//values.put("operadora", dados.getOperadora());

		getWritableDatabase().insert("Ligacoes", null, values);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String ddl = "CREATE TABLE Ligacoes (id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "idContato INT, nome TEXT NOT NULL, telefone TEXT,foto TEXT);";
		db.execSQL(ddl);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String ddl = "DROP TABLE IF EXISTS Ligacoes";
		db.execSQL(ddl);
		this.onCreate(db);
	}

	public List<Ligacao> getListaLigacao() {
		String[] colunas = { "id", "nome","telefone","idContato","foto"};
		Cursor cursor = getWritableDatabase().query("Ligacoes", colunas, null,
				null, null, null, null);

		ArrayList<Ligacao> ligacoes = new ArrayList<Ligacao>();
		
		while (cursor.moveToNext()) {
			Ligacao ligacao = new Ligacao();

			ligacao.setId(cursor.getLong(0));
			ligacao.setNome(cursor.getString(1));
			ligacao.setTelefone(cursor.getString(2));
			ligacao.setIdContato(cursor.getLong(3));
			ligacao.setFoto(cursor.getString(4));
			Log.i("CURSOR", ligacao.getId().toString());
			Log.i("TESTE", ligacao.getIdContato().toString());
			//ligacao.setHoraLigacao(cursor.getString(3));
			//ligacao.setOperadora(cursor.getString(4));
			ligacoes.add(ligacao);

		}
		return ligacoes;
	}
	public void removeAll() {	
		SQLiteDatabase db = getWritableDatabase(); // helper is object extends SQLiteOpenHelper7
		db.delete("Ligacoes", null, null);
	}
}
