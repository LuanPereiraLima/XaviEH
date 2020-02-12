package ufc.br.xavieh.exceptions;

public class CloneRepositoryException extends Exception{
	private static final long serialVersionUID = 1L;

	public CloneRepositoryException() {
		super("Ocorreu um problema ao copiar ao Clonar o repositório, tente novamente mais tarde.");
	}
}
