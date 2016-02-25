package com.elderbyte.server.vidada.tags.relations;

import com.elderbyte.server.vidada.tags.Tag;
import org.apache.commons.io.FileUtils;


import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class TagRelationDefinitionParser {



    public TagRelationDefinitionParser(){

    }


	/***************************************************************************
	 *                                                                         *
	 * Public API                                                              *
	 *                                                                         *
	 **************************************************************************/


	public TagRelationDefinition parse(File file) throws IOException, ParseException{
		return parse(FileUtils.readFileToString(file));
	}

	public TagRelationDefinition parse(String strdefinition) throws ParseException {

		TagRelationDefinition definition = new TagRelationDefinition();
		List<Token> tokens = tokenize(strdefinition);


		boolean indeclList = false;
		Tag leftTag = null;
		TagRelationOperator relation = null;

		String currentGroupName = null;
		Set<Tag> currentGroup = null;

		for (Token token : tokens) {

			if(token.type == TokenType.NEWLINE) continue;

			if(token.type == TokenType.DECL_LIST){
				// new declaration starts

				// end the old one
				if(currentGroup != null){
					definition.addNamedGroupTags(currentGroupName, currentGroup);
				}

				// init the new one
				leftTag = null;
				relation = null;
				currentGroupName = null;
				currentGroup = null;
				indeclList = true;
				continue;
			}

			if(indeclList){
				if(token.type == TokenType.LITERAL_STRING){
					Optional<Tag> rightTag = Tag.buildTag(token.value);
					if(rightTag.isPresent()){
						if(leftTag != null){
							// Create relation
							if(relation != null){
								definition.addRelation( new TagRelation(leftTag, relation, rightTag.get()));
							}else{

								// we have a tag list without relations, add it to the current group.
								currentGroup = currentGroup != null ? currentGroup : new HashSet<>();
								currentGroup.add(leftTag);
								currentGroup.add(rightTag.get());
							}
						}
						leftTag = rightTag.get();
					}
				}else if (token.type == TokenType.OPERATOR_PARENTOF) {
					relation = TagRelationOperator.IsParentOf;
				}else if (token.type == TokenType.OPERATOR_EQUALITY) {
					relation = TagRelationOperator.Equal;
				}
			}
		}

		if(currentGroup != null){
			definition.addNamedGroupTags(currentGroupName, currentGroup);;
		}

		return definition;
	}


	/***************************************************************************
	 *                                                                         *
	 * Private implementation                                                  *
	 *                                                                         *
	 **************************************************************************/


	private List<Token> tokenize(String definition) throws ParseException{

		List<Token> tokens = new ArrayList<Token>();
		String[] lines = definition.split("\\r?\\n");


		for (String lineStr : lines) {
			if(lineStr.trim().startsWith("#"))
				continue; // Skip Comment

			// tokenize the line
			String[] words = lineStr.split(" ");


			for (String word : words) {
				TokenType tokenType = isSimpleToken(word);

				if(tokenType == TokenType.NONE){
					// Not a simple token
					if(isLiteralString(word))
						tokenType = TokenType.LITERAL_STRING;
					else {
						throw new ParseException("Illegal charachter in token '" + word + "'");
					}
				}
				tokens.add(new Token(tokenType, word));
			}
			tokens.add(Token.NEWLINE);
		}
		return tokens;
	}

	static private Pattern literalStringPattern = Pattern.compile("[a-zA-Z0-9\\.]");
	private boolean isLiteralString(String word) {
		return literalStringPattern.matcher(word).find();
	}

	private TokenType isSimpleToken(String word){
		TokenType type = simpleTokenMap.get(word);
		if(type == null)
			type = TokenType.NONE;
		return type;
	}

	/***************************************************************************
	 *                                                                         *
	 * Inner classes                                                           *
	 *                                                                         *
	 **************************************************************************/


	public static class ParseException extends Exception {
		public ParseException(String message){
			super(message);
		}
	}

	private static final Map<String, TokenType> simpleTokenMap = new HashMap<String, TokenType>();

	static {
		for (TokenType type : TokenType.values()) {
			simpleTokenMap.put(type.symbol, type);
		}
	}


	private static class Token {

		final static Token NEWLINE = new Token(TokenType.NEWLINE, "");

		final TokenType type;
		final String value;


		public Token(TokenType type, String value) {
			super();
			this.type = type;
			this.value = value;
		}

		public TokenType getType() {
			return type;
		}
		public String getValue() {
			return value;
		}

		@Override
		public String toString(){
			return "["+type+": '"+getValue()+"']";
		}
	}

	private enum TokenType {

		NONE(""),

		NEWLINE(""),
		LITERAL_STRING(""),
		OPERATOR_PARENTOF(">"),
		OPERATOR_EQUALITY("="),
		DECL_LIST(":");

		String symbol;

		TokenType(String symbol){
			this.symbol = symbol;
		}
	}

}
