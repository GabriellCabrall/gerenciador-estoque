package models;

public class Produto {
	private int id;
	private String nome;
	private String descricao;
	private int quantidadeEmEstoque;
	private double precoCompra;
	private double precoVenda;
	private Categoria categoria;
	
	// Construtor
	public Produto(String nome, String descricao, int quantidadeEmEstoque, double precoCompra, double precoVenda, Categoria categoria) {
		this.nome = nome;
		this.descricao = descricao;
		this.quantidadeEmEstoque = quantidadeEmEstoque;
		this.precoCompra = precoCompra;
		this.precoVenda = precoVenda;
		this.categoria = categoria;
	}
	
	// getters e setters
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public int getQuantidadeEmEstoque() {
		return quantidadeEmEstoque;
	}
	
	public void setQuantidadeEmEstoque(int quantidadeEmEstoque) {
		this.quantidadeEmEstoque = quantidadeEmEstoque;
	}
	
	public double getPrecoCompra() {
		return precoCompra;
	}
	
	public void setPrecoCompra(double precoCompra) {
		this.precoCompra = precoCompra;
	}
	
	public double getPrecoVenda() {
		return precoVenda;
	}
	
	public void setPrecoVenda(double precoVenda) {
		this.precoVenda = precoVenda;
	}
	
	public Categoria getCategoria() {
		return categoria;
	}
	
	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
}
