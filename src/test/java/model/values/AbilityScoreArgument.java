package model.values;

import tools.TestArguments;

public abstract class AbilityScoreArgument implements TestArguments<AbilityScore>{
	protected final Integer value;
	protected final boolean isDefined;
	protected AbilityScoreArgument(Integer value, boolean isDefined){
		this.value = value;
		this.isDefined = isDefined;
	}
	
	@Override
	public String toString() {
		return String.format("%d (defined: %b)", this.value, this.isDefined);
	}
}