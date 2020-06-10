/**
 * 
 */
package model.creatures;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import model.creatures.CreatureParameters.AbilityName;
import model.values.AbilityScore;
import model.values.Value;
import model.values.ValueParameters;

/**
 * Set of the six abilities of a creature.
 * @author TLM
 */
public interface AbilityScores extends Iterable<Map.Entry<AbilityName, AbilityScore>>{
	/**
	 * Unmodifiable subset of abilities that all 
	 * creatures must have. The other abilities are optional, some creatures 
	 * may not have them.
	 */
	public final static Set<AbilityName> MANDATORY_ABILITIES = Collections.unmodifiableSet(EnumSet.of(
			AbilityName.DEXTERITY, AbilityName.WISDOM, AbilityName.CHARISMA));
	
	/**
	 * Describe the reason why  an {@link AbilityScore} object is invalid.
	 * @author TLM
	 */
	static enum InvalidityCode{
		/**The required ability is missing.	 */
		MISSING("This required ability is missing"),
		/** The ability is below the minimum value. */
		TOO_LOW("This ability is below the minimum value (" + ValueParameters.MIN_ABILITY_SCORE + ")"),
		/** The ability is above the maximum value. */
		TOO_HIGH("This ability is above the maximum value (" + ValueParameters.MAX_ABILITY_SCORE + ")");
		
		private final String errorMessage;
		private InvalidityCode(String s) {
			this.errorMessage = s;
		}
		
		/** @return the error message associated with the invalidity code. */
		String getErrorMessage() {
			return this.errorMessage;
		}
	}
	
	/**
	 * Returns the modifier associated with the given ability.
	 * @param ability
	 * @return	an integer which may be positive or negative depending on the 
	 * value of the ability, or zero if the ability is not defined.
	 */
	public int getModifier(AbilityName ability);
	
	/**
	 * Returns the {@link AbilityScore}. May return 
	 * {@link null} if the ability is not defined for the creature.
	 * @param ability queried.
	 * @return an {@link AbilityScore} object or {@link null}.
	 */
	public AbilityScore getScore(AbilityName ability);
	
	/**
	 * Checks that the input is valid to build an AbilityScore. The input is 
	 * valid if the returned map is empty.
	 * @param values	the input to check.
	 * @return	a map containing, for each invalid ability score, the reason 
	 * why it's invalid.
	 * @throws NullPointerException if value if null.
	 */
	public static Map<AbilityName, InvalidityCode> checkAbilityScoresValidity(Map<AbilityName, Integer> values) {
		//Reject null input
		if(values == null) {
			throw new NullPointerException();
		}
		Map<AbilityName, InvalidityCode> result = new EnumMap<>(AbilityName.class);
		//Check if all mandatory abilities are present
		if(!values.keySet().containsAll(MANDATORY_ABILITIES)) {
			//If not, find the missing one and throw an exception
			for(AbilityName ability: MANDATORY_ABILITIES) {
				if (!values.containsKey(ability)) {
					result.put(ability, InvalidityCode.MISSING);
				}
			}
		}
		//Verify that all values are valid
		for(Entry<AbilityName, Integer> entry : values.entrySet()) {
			if(entry.getValue() < ValueParameters.MIN_ABILITY_SCORE 
					|| entry.getValue() > ValueParameters.MAX_ABILITY_SCORE) {
				result.put(entry.getKey(),
						entry.getValue() < ValueParameters.MIN_ABILITY_SCORE ? 
								InvalidityCode.TOO_LOW : InvalidityCode.TOO_HIGH);
			}
		}
		return result;
	}
	
	/**
	 * Checks that the AbilityScores object is valid as per 
	 * {@link AbilityScores#checkAbilityScoresValidity(Map)}.
	 * @return a map containing for each invalid ability the reason why it is 
	 * invalid.
	 */
	public Map<AbilityName, InvalidityCode> checkValidity();

	/**
	 * Initialises an AbilityScores object.
	 * @param values	map assigning the base scores to the abilities. May be 
	 * empty or null.
	 */
	public static AbilityScores create(Map<AbilityName, Integer> values) {
		return new RWAbilityScores(values);
	}
}

/**
 * Read-only version of the {@link AbilityScores} interface. This class
 * encapsulates a read-write implementation of the interface. Any change 
 * brought to the read-write object is reflected by the read-only one.
 * @author TLM
 */
class ROAbilityScores implements AbilityScores{
	//Read-write ability scores encapsulated in this object.
	private RWAbilityScores abilities;
	
	/**
	 * Initialise a {@link ROAbilityScores} object encapsulating a 
	 * {@link RWAbilityScores} object.
	 * @param abilities RW object to encapsulate.
	 */
	protected ROAbilityScores(RWAbilityScores abilities){
		this.abilities = abilities;
	}

	@Override
	public int getModifier(AbilityName ability) {
		return abilities.getModifier(ability);
	}

	@Override
	public AbilityScore getScore(AbilityName ability) {
		return abilities.getScore(ability);
	}

	@Override
	public Iterator<Map.Entry<AbilityName, AbilityScore>> iterator() {
		return abilities.iterator();
	}

	@Override
	public Map<AbilityName, InvalidityCode> checkValidity() {
		return abilities.checkValidity();
	}
	
}

/**
 * Read-write implementation of the {@link AbilityScores} interface.
 * This class offers additional methods to increment abilities or add bonuses.
 * Classes using a {@link RWAbilityScores} attribute should never expose it 
 * directly but only expose its {@link ROAbilityScores} wrapper.
 * @author TLM
 */
class RWAbilityScores implements AbilityScores, CommittablePart<RWAbilityScores>{
	/**
	 * Map of the scores associated with the abilities.
	 */
	private EnumMap<AbilityName, AbilityScoreType> abilities;
	private ROAbilityScores roAbilityScores = new ROAbilityScores(this);
	private boolean isPrepared = false;

	/**
	 * Initialises an {@link AbilityScores} object.
	 * @param values	map assigning the base scores to the abilities. May be 
	 * empty or null.
	 */
	public RWAbilityScores(Map<AbilityName, Integer> values) {
		this.abilities = new EnumMap<AbilityName, AbilityScoreType>(AbilityName.class);
		for(AbilityName ability: AbilityName.values()) {
			this.abilities.put(ability, new AbilityScoreType(values == null ? null : values.get(ability)));
		}
	}
	
	/**
	 * Sets the value of a given {@link AbilityScore}.
	 * @param ability	to set
	 * @param value		to assign to the ability. May be null.
	 * @return the reason why the new value is invalid if it is so, or null if 
	 * it is valid.
	 * @throws NullPointerException if ability is null.
	 */
	public InvalidityCode setAbilityScore(AbilityName ability, Integer value) {
		if(ability == null) {
			throw new NullPointerException("Tried to set the value of the null ability");
		}
		this.isPrepared = false;
		if(value == null) {
			this.abilities.get(ability).nullify();
			if(MANDATORY_ABILITIES.contains(ability)) {
				return InvalidityCode.MISSING;
			} else {
				return null;
			}
		} else {
			this.abilities.get(ability).setValue(value);
			if(value < ValueParameters.MIN_ABILITY_SCORE) {
				return InvalidityCode.TOO_LOW;
			} else if (value > ValueParameters.MAX_ABILITY_SCORE) {
				return InvalidityCode.TOO_HIGH;
			} else {
				return null;
			}
		}
	}
	
	/**
	 * Throws an {@link java.lang.IllegalStateException} describing how 
	 * the values are invalid.
	 * @param invalidity	map of the invalidity reasons. Assumed to be 
	 * neither null nor empty.
	 * @param values		map of the values that are invalid.
	 * @throws IllegalStateException
	 */
	private void rejectInvalidAbilityScoreInput(Map<AbilityName, InvalidityCode> invalidity) 
			throws IllegalStateException{
		String errorMessage = "Tried to create an invalid AbilityScores object:";
		for(Map.Entry<AbilityName, InvalidityCode> entry : invalidity.entrySet()) {
			errorMessage += " " + entry.getKey() + " : " + entry.getValue().getErrorMessage();
			switch(entry.getValue()) {
			case MISSING:
				break;
			case TOO_HIGH:
			case TOO_LOW:
				errorMessage += " : " + this.getScore(entry.getKey());
				break;
			default:
				break;
			}
			errorMessage += ";";
		}
		throw new IllegalStateException(errorMessage);
	}
	
	/**
	 * Initialises a RWAbilityScores object by making a deep-copy of
	 * the input {@link AbilityScores} object.
	 * @param abilities	object to copy.
	 */
	public RWAbilityScores(AbilityScores abilities) {
		//Reject null input
		if(abilities == null) {
			throw new NullPointerException("Tried to deep-copy null");
		}
		this.abilities = new EnumMap<AbilityName, AbilityScoreType>(AbilityName.class);
		for(Map.Entry<AbilityName, AbilityScore> entry : abilities) {
			if(entry.getValue().isDefined()) {
				this.abilities.put(entry.getKey(), new AbilityScoreType(entry.getValue().getValue()));
			} else {
				this.abilities.put(entry.getKey(), new AbilityScoreType(10));
				this.abilities.get(entry.getKey()).isDefined = false;
			}
		}
	}
	
	@Override
	public int getModifier(AbilityName ability) {
		return abilities.get(ability).getModifier();
	}
	@Override
	public AbilityScore getScore(AbilityName ability) {
		return abilities.get(ability);
	}
	
	/**
	 * @return a read-only object encapsulating this one.
	 */
	public AbilityScores getROAbilityScores() {
		return this.roAbilityScores;
	}

	/**
	 * Implementation of the {@link AbilityScore} interface based on the 
	 * {@link Value} class. It adds mutability to the interface, which must be 
	 * managed by the {@link AbilityScores} container.
	 * @author TLM
	 */
	private static class AbilityScoreType extends Value implements AbilityScore{
		private boolean isDefined = false;
		/**
		 * Basic constructor directly derived from {@link Value#Value(int)}.
		 * @param value
		 */
		AbilityScoreType(Integer value) {
			super(value == null ? 10 : value);
			this.isDefined = value != null;
		}
		
		/**
		 * Basic constructor for an ability score that has not been defined.
		 */
		AbilityScoreType(){
			super(10);
			this.isDefined = false;
		}

		@Override
		public int getModifier() {
			if(this.isDefined) {
				return AbilityScore.computeModifier(this.getValue());
			} else {
				return 0;
			}
		}
		
		@Override
		public int getValue() {
			if(this.isDefined) {
				return super.getValue();
			} else {
				throw new UnsupportedOperationException("Tried to call getValue on the UNDEFINED ability score");
			}
		}

		@Override
		public boolean isDefined() {
			return this.isDefined;
		}
		
		@Override
		public void setValue(int value) {
			super.setValue(value);
			this.isDefined = true;
		}
		
		/**
		 * Makes the ability score undefined.
		 */
		public void nullify() {
			this.isDefined = false;
		}

		@Override
		public int compareTo(AbilityScore o) {
			//Undefined is worse than anything except undefined
			if(!this.isDefined()) {
				return o.isDefined() ? -1 : 0;
			} else if (!o.isDefined()) {
				return 1;
			}
			return this.getValue() - o.getValue();
		}
		
		@Override
		public boolean equals(Object o) {
			//Undefined only equals undefined
			return (o instanceof AbilityScore &&
					this.isDefined() == ((AbilityScore) o).isDefined() &&
					(!this.isDefined() ||
							(this.isDefined() && this.getValue() == ((AbilityScore) o).getValue()))
					);
		}
		
		@Override
		public int hashCode() {
			if(this.isDefined()) {
				return ((Integer) this.getValue()).hashCode();
			} else {
				return ((Boolean) false).hashCode();
			}
		}

	}

	@Override
	public Iterator<Map.Entry<AbilityName, AbilityScore>> iterator() {
		return new Iterator<Map.Entry<AbilityName, AbilityScore>>(){
			//Build atop an iterator for the names
			Iterator<AbilityName> nameIterator = EnumSet.allOf(AbilityName.class).iterator();
			@Override
			public boolean hasNext() {
				return nameIterator.hasNext();
			}

			@Override
			public Entry<AbilityName, AbilityScore> next() {
				AbilityName name = nameIterator.next();
				return new AbstractMap.SimpleImmutableEntry<AbilityName, AbilityScore>(name, getScore(name));
			}
		};
	}

	@Override
	public Map<AbilityName, InvalidityCode> checkValidity() {
		return AbilityScores.checkAbilityScoresValidity(
				//Convert Map<AbilityName, AbilityScores> into Map<AbilityName, Integer>
				this.abilities.entrySet().stream()
				.filter(e -> e.getValue().isDefined) //keep only the defined values
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getValue())));
	};
	
	@Override
	public void commit(RWAbilityScores toReplace) throws IllegalStateException{
		if(!this.isPrepared) {
			throw new IllegalStateException("Tried to commit an unprepared object");
		}
		//copy this into toReplace
		for(AbilityName ability: AbilityName.values()) {
			if(this.abilities.get(ability).isDefined) {
				toReplace.setAbilityScore(ability, this.getScore(ability).getValue());
			} else {
				toReplace.abilities.get(ability).nullify();
			}
		}
	}
	
	@Override
	public void prepareCommit() throws IllegalStateException{
		Map<AbilityName, InvalidityCode> errors = this.checkValidity();
		if(errors.isEmpty()) {
			this.isPrepared = true;
		} else {
			rejectInvalidAbilityScoreInput(errors);
		}
	}
}