package me.xjunior.remoteworks.script;

public class ScriptParam {
	public enum Type {
		STRING("string"), NUMBER("boolean"), BOOLEAN("number");
		
		String str;
		
		Type(String str) {
			this.str = str;
		}
		
		public boolean isValidValue(String value) {
			switch (this) {
			case NUMBER:
				return (""+Double.parseDouble(value)).equals(value);
			case BOOLEAN:
				return value.equals("0") || value.equals("1");
			case STRING:
				return true;
			}
			return false;
		}
		
		public String toString() {
			return str;
		}
		
		public static Type parse(String _t) {
			if (STRING.toString().equals(_t))
				return Type.STRING;
			else if (BOOLEAN.toString().equals(_t))
				return Type.BOOLEAN;
			else if (NUMBER.toString().equals(_t))
				return Type.NUMBER;
			else return null;
		}
	}
	
	private String name;
	private Type type;
	
	public ScriptParam(String _n, Type _t) {
		name = _n;
		type = _t;
	}
	
	public String getName() {
		return name;
	}
	
	public Type getType() {
		return type;
	}
	
	public boolean equals(ScriptParam param) {
		return param.getType() == type && param.getName().equals(name);
	}
}