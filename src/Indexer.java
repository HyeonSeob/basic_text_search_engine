import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.tartarus.snowball.ext.PorterStemmer;

public class Indexer
{
	public static void main(String[] args) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(args[0]));
		Analyzer analyzer = new StandardAnalyzer();
		FSDirectory index = FSDirectory.open(Paths.get("index"));
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter w = new IndexWriter(index, config);
		
		int id;
		String line;
		String[] stklist;
		StringBuffer title = new StringBuffer();
		StringBuffer body = new StringBuffer();
		PorterStemmer stemmer = new PorterStemmer();
		
		line = br.readLine();
		while(line != null){
			title.setLength(0);
			body.setLength(0);
			
			stklist = line.split(" ");
			id = Integer.parseInt(stklist[1]);
			System.out.println("id: "+id);
			
			br.readLine();
			while(!(line = br.readLine()).equals(".A"))
			{
				stklist = line.split("\\,|\\.| ");
				for(String stk : stklist)
				{
					stemmer.setCurrent(stk);
					stemmer.stem();
					title.append(stemmer.getCurrent());
					title.append(" ");
				}
			}
			System.out.println("title: "+title);
			
			
			while(!br.readLine().equals(".W"));
			while((line = br.readLine()) != null)
			{
				if(line.substring(0, 2).equals(".I"))
					break;
				
				stklist = line.split("\\,|\\.| ");
				for(String stk : stklist)
				{
					stemmer.setCurrent(stk);
					stemmer.stem();
					body.append(stemmer.getCurrent());
					body.append(" ");
				}
			}
			System.out.println("body: "+body);
			
			addDoc(w, id, title.toString(), body.toString());
		}
		
		w.close();
		
	}
	
	private static void addDoc(IndexWriter w, int id, String title, String body) throws IOException
	{
		Document doc = new Document();
		doc.add(new IntField("id", id, Field.Store.YES));
		doc.add(new TextField("title", title, Field.Store.YES));
		doc.add(new StringField("body", body, Field.Store.YES));
		w.addDocument(doc);
	}
}