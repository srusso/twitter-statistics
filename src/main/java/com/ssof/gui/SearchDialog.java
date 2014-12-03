package com.ssof.gui;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import com.ssof.exceptions.DateFormatException;
import com.ssof.tweetsearch.SearchParameters;
import com.ssof.tweetsearch.TweetSearch;
import com.ssof.twitter.SingleTweet;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Choice;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class SearchDialog extends JFrame implements MouseListener{
	private class PlaceSources{
		Collection <String> places;
		Collection <String> sources;
		public PlaceSources(Collection <String> places, Collection <String> sources){
			this.places  = places;
			this.sources = sources;
		}
	}
	
	
	private final MainWindow parent;
	private final List<SingleTweet> tweets;

	private static final long serialVersionUID = 7783611043919914891L;
	private JPanel contentPane;
	private final JTextField txtUnaOPi = new JTextField();
	private JTextField textField;
	private JTextField textField_1;
	private JTextField txtLaFraseEsatta;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField txtDalGiorno;
	private JTextField txtAlGiorno;
	private JTextField txtGgmmaaaa;
	private JTextField txtGgmmaaaa_1;
	private JTextField txtAutore;
	private JTextField txtLocalit;
	private JTextField txtSorgente;
	private JTextField textField_4;
	private Choice list;
	private Choice list_1;
	private JButton btnEffettuaRicerca;
	private JButton btnAnnulla;

	/**
	 * Create the frame.
	 */
	public SearchDialog(MainWindow parent, List <SingleTweet> tweets) {
		this.parent = parent;
		this.tweets = tweets;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 700, 381);
		setLocationRelativeTo(parent);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(53dlu;default):grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(157dlu;default):grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		this.setTitle("Ricerca nei tweet caricati");
		
		txtUnaOPi.setEditable(false);
		txtUnaOPi.setText("Una o pi\u00F9 parole:");
		contentPane.add(txtUnaOPi, "2, 2");
		txtUnaOPi.setColumns(10);
		
		textField = new JTextField();
		contentPane.add(textField, "4, 2, fill, default");
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setText("Tutte le seguenti parole:");
		textField_1.setEditable(false);
		textField_1.setColumns(10);
		contentPane.add(textField_1, "2, 4, fill, default");
		
		textField_2 = new JTextField();
		contentPane.add(textField_2, "4, 4, fill, default");
		textField_2.setColumns(10);
		
		txtLaFraseEsatta = new JTextField();
		txtLaFraseEsatta.setEditable(false);
		txtLaFraseEsatta.setText("La frase esatta: ");
		contentPane.add(txtLaFraseEsatta, "2, 6, fill, default");
		txtLaFraseEsatta.setColumns(10);
		
		textField_3 = new JTextField();
		contentPane.add(textField_3, "4, 6, fill, default");
		textField_3.setColumns(10);
		
		txtDalGiorno = new JTextField();
		txtDalGiorno.setEditable(false);
		txtDalGiorno.setText("Dal giorno [gg/mm/aaaa]: ");
		contentPane.add(txtDalGiorno, "2, 8, fill, default");
		txtDalGiorno.setColumns(10);
		
		txtGgmmaaaa = new JTextField();
		txtGgmmaaaa.setToolTipText("gg/mm/aaaa");
		contentPane.add(txtGgmmaaaa, "4, 8, fill, default");
		txtGgmmaaaa.setColumns(10);
		
		txtAlGiorno = new JTextField();
		txtAlGiorno.setEditable(false);
		txtAlGiorno.setText("Al giorno [gg/mm/aaaa]: ");
		contentPane.add(txtAlGiorno, "2, 10, fill, default");
		txtAlGiorno.setColumns(10);
		
		txtGgmmaaaa_1 = new JTextField();
		txtGgmmaaaa_1.setToolTipText("gg/mm/aaaa");
		contentPane.add(txtGgmmaaaa_1, "4, 10, fill, default");
		txtGgmmaaaa_1.setColumns(10);
		
		txtAutore = new JTextField();
		txtAutore.setEditable(false);
		txtAutore.setText("Autore:");
		contentPane.add(txtAutore, "2, 12, fill, default");
		txtAutore.setColumns(10);
		
		textField_4 = new JTextField();
		contentPane.add(textField_4, "4, 12, fill, default");
		textField_4.setColumns(10);
		
		txtLocalit = new JTextField();
		txtLocalit.setEditable(false);
		txtLocalit.setText("Localit\u00E0:");
		contentPane.add(txtLocalit, "2, 14, fill, default");
		txtLocalit.setColumns(10);
		
		PlaceSources ps = getTweetPlacesAndSources();
		
		list = new Choice();
		Collection <String> places = ps.places;
		Iterator <String> placeIterator = places.iterator();
		while(placeIterator.hasNext()){
			list.addItem(placeIterator.next());
		}
		contentPane.add(list, "4, 14, fill, fill");
		
		txtSorgente = new JTextField();
		txtSorgente.setEditable(false);
		txtSorgente.setText("Sorgente:");
		contentPane.add(txtSorgente, "2, 16, fill, default");
		txtSorgente.setColumns(10);
		
		list_1 = new Choice();
		Collection <String> sources = ps.sources;
		Iterator <String> sourceIterator = sources.iterator();
		while(sourceIterator.hasNext()){
			list_1.addItem(sourceIterator.next());
		}
		contentPane.add(list_1, "4, 16, fill, fill");
		
		btnAnnulla = new JButton("Chiudi");
		btnAnnulla.setName("btnannulla");
		contentPane.add(btnAnnulla, "2, 22");
		btnAnnulla.addMouseListener(this);
		
		btnEffettuaRicerca = new JButton("Effettua Ricerca");
		btnEffettuaRicerca.setName("btnricerca");
		contentPane.add(btnEffettuaRicerca, "4, 22");
		btnEffettuaRicerca.addMouseListener(this);
		
		pack();
	}
	
	/**
	 * Effettua la ricerca utilizzando i parametri inseriti dall'utente.
	 * @return True se effettua la ricerca, false altrimenti
	 */
	public boolean doSearch(){
		try {
			SearchParameters params = new SearchParameters(textField.getText(), //una o piu parole in questa stringa
					textField_2.getText(), //tutte le parole in questa stringa
					textField_3.getText(), //questa frase esatta
					txtGgmmaaaa.getText(), //dopo la data
					txtGgmmaaaa_1.getText(), //prima della data
					textField_4.getText(), //autore [case in-sensitive]
					list.getSelectedItem(), //localita [case in-sensitive]
					list_1.getSelectedItem()); //sorgente [case in-sensitive]
			
			TweetSearch ts = new TweetSearch(parent.getTweets());
			
			parent.onSearchResultsReceived(ts.getSearchResults(params)); //notifico mainwindow con i risultati della ricerca
			
		} catch (DateFormatException e) {
			JOptionPane.showMessageDialog(this, e.toString(), "Data errata", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		
		return true;
	}
	
	private PlaceSources getTweetPlacesAndSources() {
		Collection <String> places = new TreeSet<String>();
		Collection <String> sources = new TreeSet<String>();
	
		places.add("");
		sources.add("");
		
		Iterator <SingleTweet> i = tweets.iterator();
		SingleTweet tweet;
		while(i.hasNext()){
			tweet = i.next();
			if(!places.contains(tweet.place)){
				places.add(tweet.place);
			}
			if(!sources.contains(tweet.source)){
				sources.add(tweet.source);
			}
		}
		
		return new PlaceSources(places, sources);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Component comp = e.getComponent();
		String cname = comp.getName();
		
		if(cname==null)
			return;
		
		if(cname.equals(btnAnnulla.getName())){
			dispose();
		} else if(cname.equals(btnEffettuaRicerca.getName())){
			boolean x = doSearch();
			if(x) dispose();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

}
