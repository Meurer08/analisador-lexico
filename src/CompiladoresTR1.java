import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

public class CompiladoresTR1 {

    enum TipoToken {//regez para captura
        RESERVADA("^int|double|char|float|if|while|for", "reservada"),
        IDENTIFICADOR("[A-Z][a-zA-Z]*", "identificador"),
        NUMERO("(\\d+(,\\d+)?)", "número"),
        OPERADOR("^<=|>=|==|=|<|>", "operador"),
        SIMBOLO("[();]", "simbolo"),
        WHITESPACE("^[ \t\r\n]+", "espaco em branco"),
        COMENTARIO("^#.*", "comentario");

        public final Pattern pattern;
        public final String nome;

        TipoToken(String regex, String nome) {
            this.pattern = Pattern.compile(regex);
            this.nome = nome;
        }
    }

    static class Token {
        TipoToken type;
        String value;
        int entrada = 0;

        Token(TipoToken type, String value) {
            this.type = type;
            this.value = value;
        }
    }

    public static List<Token> tokensExtraidosImput(String input) {

        List<Token> tokens = new ArrayList<>();
        var cleanInputList = input.replaceAll("#", "\n#")
                .lines()
                .filter(s -> !s.matches(TipoToken.COMENTARIO.pattern.toString()) && !s.isBlank())
                .toList();


        for (String s : cleanInputList) {

            for (TipoToken type : TipoToken.values()) {
                String regex = type.pattern.pattern();
                Pattern pattern = Pattern.compile(regex);

                String substring = s.trim();
                Matcher matcher = pattern.matcher(substring);
                switch (type) {
                    case IDENTIFICADOR -> findTokens(type, matcher, tokens);
                    case RESERVADA -> findTokens(type, matcher, tokens);
                    case NUMERO -> findTokens(type, matcher, tokens);
                    case OPERADOR -> findTokens(type, matcher, tokens);
                    case SIMBOLO -> findTokens(type, matcher, tokens);
                    case WHITESPACE -> findTokens(type, matcher, tokens);
                    case COMENTARIO -> findTokens(type, matcher, tokens);
                }
            }
        }

        return tokens;
    }

    private static void findTokens(TipoToken type, Matcher matcher, List<Token> tokens) {
        while (matcher.find()) {
            String value = matcher.group();
    
            // Só adiciona se não for espaço e ainda não estiver na lista
            if (type != TipoToken.WHITESPACE &&
                tokens.stream().noneMatch(token -> token.type == type && Objects.equals(token.value, value))) {
    
                tokens.add(new Token(type, value));
            }
        }
    }

    public static void main(String[] args) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder sourceCode = new StringBuilder();
        String line;
        
        System.out.println("Ctrl+D para enviar:");

        while ((line = reader.readLine()) != null) {
            sourceCode.append(line).append("\n");
        }
        System.out.println(sourceCode);
        List<Token> tokens = tokensExtraidosImput(sourceCode.toString());


        System.out.println("\nLista de Simbolos:");
        int count = 1;
        for (Token token : tokens) {

           if (!("operador".equals(token.type.nome) || "simbolo".equals(token.type.nome))) {
                token.entrada = count;
            
                System.out.println(new StringBuilder()
                    .append(count++)
                    .append(" | ")
                    .append(token.value)
                    .append(" | ")
                    .append(token.type.nome));
            
                
           }
        }

        String textoLimpo = sourceCode.toString().replaceAll("#", "\n#")
                .lines()
                .filter(s -> !s.matches(TipoToken.COMENTARIO.pattern.toString()) && !s.isBlank())
                .collect(Collectors.joining());

        for (Token token : tokens) {
            if (token.entrada != 0) {
                textoLimpo = textoLimpo.replaceAll(Pattern.quote(token.value), "__TOKEN_" + token.entrada + "__");
                } else {
                    textoLimpo = textoLimpo.replaceAll(Pattern.quote(token.value), "__LITERAL_" + token.value + "__");
                }
            }
                
        for (Token token : tokens) {
            if (token.entrada != 0) {
                textoLimpo = textoLimpo.replace("__TOKEN_" + token.entrada + "__", "<" + token.type.nome + "," + token.entrada + ">");
            } else {
                textoLimpo = textoLimpo.replace("__LITERAL_" + token.value + "__", "<" + token.value + ",>");
            }
        }

        //textoLimpo = textoLimpo.replaceAll(">([^<]+)",  " ");

        Pattern pattern = Pattern.compile("<<?[^<>]+>>?");
        Matcher matcher = pattern.matcher(textoLimpo);

        while (matcher.find()) {
            System.out.printf(matcher.group());
        }
    }
}