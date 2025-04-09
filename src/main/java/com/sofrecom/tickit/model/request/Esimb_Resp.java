package com.sofrecom.tickit.model.request;

public class Esimb_Resp {
	
	private String codeBanbou;
	private String codeIMB;
	private String dateVerification;
	
	
	   private String idacte;
	    private String affectation;
		private int duree;
		private int quantite;
		private String dateLivraison;
		private String commentaire;
		private String motif;
		
		
		
		public String getCodeBanbou() {
			return codeBanbou;
		}
		public void setCodeBanbou(String codeBanbou) {
			this.codeBanbou = codeBanbou;
		}
		public String getCodeIMB() {
			return codeIMB;
		}
		public void setCodeIMB(String codeIMB) {
			this.codeIMB = codeIMB;
		}
		public String getDateVerification() {
			return dateVerification;
		}
		public void setDateVerification(String dateVerification) {
			this.dateVerification = dateVerification;
		}
		
		public String getIdacte() {
			return idacte;
		}
		public void setIdacte(String idacte) {
			this.idacte = idacte;
		}
		public String getAffectation() {
			return affectation;
		}
		public void setAffectation(String affectation) {
			this.affectation = affectation;
		}
		public int getDuree() {
			return duree;
		}
		public void setDuree(int duree) {
			this.duree = duree;
		}
		
		public int getQuantite() {
			return quantite;
		}
		public void setQuantite(int quantite) {
			this.quantite = quantite;
		}
		public String getDateLivraison() {
			return dateLivraison;
		}
		public void setDateLivraison(String dateLivraison) {
			this.dateLivraison = dateLivraison;
		}
		public String getCommentaire() {
			return commentaire;
		}
		public void setCommentaire(String commentaire) {
			this.commentaire = commentaire;
		}
		
	
	
		public String getMotif() {
			return motif;
		}
		public void setMotif(String motif) {
			this.motif = motif;
		}
		
	
		public Esimb_Resp(String codeBanbou, String codeIMB, String dateVerification, String idacte,
				 String affectation,
				int duree, String commentaire) {
			super();
			this.codeBanbou = codeBanbou;
			this.codeIMB = codeIMB;
			this.dateVerification = dateVerification;
			this.idacte = idacte;
			this.affectation = affectation;
			this.duree = duree;
			this.commentaire = commentaire;
		}
		/*public Esimb_Resp(String codeBanbou, String codeIMB, String dateVerification, String idacte,
				String affectation, int duree, String commentaire) {
			super();
			this.codeBanbou= codeBanbou;
			this.codeIMB = codeIMB;
			this.dateVerification = dateVerification;
			this.idacte = idacte;
			this.affectation = affectation;
			this.duree = duree;
			this.commentaire = commentaire;
		}*/
		public Esimb_Resp(String codeBanbou, String codeIMB, String dateVerification, String idacte,
				String affectation, int duree, int quantite, String dateLivraison, String commentaire, String motif) {
			super();
			this.codeBanbou= codeBanbou;
			this.codeIMB = codeIMB;
			this.dateVerification = dateVerification;
			this.idacte = idacte;
			this.affectation = affectation;
			this.duree = duree;
			this.quantite = quantite;
			this.dateLivraison = dateLivraison;
			this.commentaire = commentaire;
			this.motif = motif;
		}
		public Esimb_Resp() {
		}
	
	
		
		

}
