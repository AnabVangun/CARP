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

import model.exceptions.IllegalAbilityScoreException;
import model.values.AbilityScore;
import model.values.Value;
import service.parameters.CreatureParameters.AbilityName;
import service.parameters.ValueParameters;

/**
 * Set of the six abilities of any creature.
 * @author TLM
 */
public interface AbilityScores extends Iterable<Map.Entry<AbilityName, AbilityScore>>{
	/**
	 * Public unmodifiable reference to the subset of abilities that all 
	 * creatures must have. The other abilities are optional, some creatures 
	 * may not have them.
	 */
	public final static Set<AbilityName> MANDATORY_ABILITIES = Collections.unmodifiableSet(EnumSet.of(
			AbilityName.DEXTERITY, AbilityName.WISDOM, AbilityName.CHARISMA));
	
	/**
	 * Returns the modifier associated with the given ability.
	 * @param ability
	 * @return	an integer which may be positive or negative depending on the 
	 * value of the ability, or zero if the ability is not defined.
	 */
	public int getModifier(AbilityName ability);
	
	/**
	 * Returns specified the {@link model.values.AbilityScore}. May return 
	 * {@link null} if the ability is not defined for the creature.
	 * @param ability
	 * @return an {@link AbilityScore} object or {@link null}.
	 */
	public AbilityScore getScore(AbilityName ability);
	
	/**
	 * Checks that the input is valid to build an AbilityScore.
	 * @param values	the input to check.
	 * @param fail		if true, raise an {@link IllegalAbilityScoreException}
	 * instead of returning false.
	 * @return	true if all mandatory abilities are present, and all present 
	 * abilities have valid values.
	 */
	public static boolean isValidAbilityScoreInput(Map<AbilityName, Integer> values, boolean fail) {
		//Reject null input
		if(values == null) {
			if(fail) {
				throw new IllegalAbilityScoreException(IllegalAbilityScoreException.Cause.NULL);
			}
			return false;
		}
		//Check if all mandatory abilities are present
		if(!MANDATORY_ABILITIES.containsAll(values.keySet())) {
			//If not, find the missing one and throw an exception
			for(AbilityName ability: MANDATORY_ABILITIES) {
				if (!values.containsKey(ability)) {
					if(fail) {
						throw new IllegalAbilityScoreException(ability.toString(), values.keySet().toString());
					}
					return false;
				}
			}
		}
		//Verify that all model.values are valid
		for(Entry<AbilityName, Integer> entry : values.entrySet()) {
			if(entry.getValue() < ValueParameters.MIN_ABILITY_SCORE 
					|| entry.getValue() > ValueParameters.MAX_ABILITY_SCORE) {
				if(fail) {
					throw new IllegalAbilityScoreException(entry.getKey().toString(), entry.getValue());
				}
				return false;
			}
		}
		return true;
	}

	/**
	 * Initialises an {@link AbilityScores} object with model.values for at least 
	 * some of the abilities.
	 * @param model.values	must contain the mandatory abilities: DEXTERITY, 
	 * WISDOM, and CHARISMA. May also contain the optional abilities.
	 * @throws {@link model.exceptions.IllegalAbilityScoreException} if a mandatory 
	 * ability is missing or if a value is invalid.
	 */
	public static AbilityScores create(Map<AbilityName, Integer> values) {
		return new RWAbilityScores(values);
	}
}

/**
 * Read-only version of the {@link AbilityScores} interface. This class
 * encapsulates a read-write implementation of the interface. Any change 
 * brought to the RW object is reflected by the RO one.
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
	
}

/**
 * Read-write implementation of the {@link AbilityScores} interface.
 * This class offers additional methods to increment abilities or add bonuses.
 * Classes using a {@link RWAbilityScores} attribute should never expose it 
 * directly but only expose its {@link ROAbilityScores} counterpart.
 * @author TLM
 */
class RWAbilityScores implements AbilityScores{
	/**
	 * Map of the scores associated with the abilities.
	 */
	private EnumMap<AbilityName, AbilityScoreType> abilities;

	/**
	 * Initialises an {@link AbilityScores} object with model.values for at least 
	 * some of the abilities.
	 * @param model.values	must contain the mandatory abilities: DEXTERITY, 
	 * WISDOM, and CHARISMA. May also contain the optional abilities.
	 * @throws {@link model.exceptions.IllegalAbilityScoreException} if a mandatory 
	 * ability is missing or if a value is invalid.
	 */
	public RWAbilityScores(Map<AbilityName, Integer> values) {
		//Validate input
		AbilityScores.isValidAbilityScoreInput(values, true);
		this.abilities = new EnumMap<AbilityName, AbilityScoreType>(AbilityName.class);
		for(Entry<AbilityName, Integer> entry : values.entrySet()) {
			this.abilities.put(entry.getKey(), new AbilityScoreType(entry.getValue()));
		}
	}
	
	/**
	 * Initialises a {@link RWAbilityScores} object by making a deep-copy of
	 * the input {@link AbilityScores} object.
	 * @param abilities	object to copy.
	 */
	public RWAbilityScores(AbilityScores abilities) {
		//Reject null input
		if(abilities == null) {
			throw new IllegalAbilityScoreException(IllegalAbilityScoreException.Cause.NULL);
		}
		this.abilities = new EnumMap<AbilityName, AbilityScoreType>(AbilityName.class);
		for(Map.Entry<AbilityName, AbilityScore> entry : abilities) {
			if(entry.getValue() != null) {
				this.abilities.put(entry.getKey(), new AbilityScoreType(entry.getValue()));
			}
		}
	}
	
	@Override
	public int getModifier(AbilityName ability) {
		return abilities.getOrDefault(ability, AbilityScoreType.UNDEFINED).getModifier();
	}
	@Override
	public AbilityScore getScore(AbilityName ability) {
		return abilities.get(ability);
	}
	
	/**
	 * @return a read-only object encapsulating this one.
	 */
	public AbilityScores getROAbilityScores() {
		return new ROAbilityScores(this);
	}

	/**
	 * Implementation of the {@link AbilityScore} interface based on the 
	 * {@link Value} class. It adds mutability to the interface, which must be 
	 * managed by the {@link AbilityScores} container.
	 * @author TLM
	 */
	private static class AbilityScoreType extends Value implements AbilityScore{
		
		final static AbilityScoreType UNDEFINED = new AbilityScoreType(ValueParameters.MIN_ABILITY_SCORE) {
			@Override
			public int getModifier() {
				return 0;
			}
			@Override
			public int getValue() {
				throw new IllegalAbilityScoreException(IllegalAbilityScoreException.Cause.UNDEFINED);
			}
		};
		/**
		 * Basic constructor directly derived from {@link Value#Value(int)}.
		 * @param value
		 */
		AbilityScoreType(int value) {
			super(value);
		}
		
		/**
		 * Makes a deep copy of the input {@link AbilityScore} object.
		 * @param value	to copy.
		 */
		AbilityScoreType(AbilityScore value){
			//XXX This should probably fail if any bonuses apply to value
			super(value.getValue());
		}

		@Override
		public int getModifier() {
			return AbilityScore.computeModifier(this.getValue());
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
	};
}