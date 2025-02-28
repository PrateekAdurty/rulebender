package rulebender.contactmap.models;

import java.util.HashMap;

import rulebender.editors.bngl.model.BNGLModelBuilderInterface;
import rulebender.editors.bngl.model.ruledata.BondActionData;
import rulebender.editors.bngl.model.ruledata.ComponentData;
import rulebender.editors.bngl.model.ruledata.MoleculePatternData;
import rulebender.editors.bngl.model.ruledata.RuleData;
import rulebender.editors.bngl.model.ruledata.RulePatternData;

/**
 * This class needs to construct and return a CMapModel object based on the
 * method calls that it receives from the BNGASTReader.
 * 
 * Uses Builder Pattern.
 * 
 * Need to produce:
 * 
 * ArrayList<Molecule>: String expression; String name; ArrayList<Component>;
 * String name; ArrayList<State>; String name; ArrayList<String>; // for
 * compartments
 * 
 * ArrayList<Bond> int molecule1; // int id is index of molecule in molecule
 * list int component1; // int id is index of component in component list for
 * molecule int state1; // int id is index of state in component list int
 * molecule2; int component2; int state2; boolean CanGenerate;
 * 
 * ArrayList<Rule> String label; // label of the rule, could be empty String
 * name; // expression of rule, including rates boolean bidirection; String
 * rate1; String rate2; ArrayList<RulePattern> reactantpatterns;
 * ArrayList<MoleculePattern> molepatterns; private int moleindex; // int id
 * into the molecules arraylist ArrayList<ComponentPattern> comppatterns; int
 * compindex; // int id into component arraylist for molecule int stateindex; //
 * int id into state arraylist for component int wildcards; // -1: None 0: ? 1:
 * + // I have no idea ArrayList<Integer> bonds; // integer id into the main
 * bond arraylist ArrayList<RulePattern> productpatterns;
 * ArrayList<MoleculePattern> molepatterns; private int moleindex; // int id
 * into the molecules arraylist ArrayList<ComponentPattern> comppatterns; int
 * compindex; // int id into component arraylist for molecule int stateindex; //
 * int id into state arraylist for component int wildcards; // -1: None 0: ? 1:
 * + // I have no idea ArrayList<BondAction> bondactions; int bondindex; //
 * index into bonds arraylist int action;// negative means delete, positive
 * means add
 * 
 * @author mr_smith22586
 * 
 */
public class CMapModelBuilder implements BNGLModelBuilderInterface {
	// The model under construction.
	ContactMapModel m_model;

	// A quick lookup for molecule ids
	HashMap<String, Integer> m_moleculeIDForName;

	/**
	 * Constructor initializes data fields.
	 */
	public CMapModelBuilder() {
		m_model = new ContactMapModel();
		m_moleculeIDForName = new HashMap<String, Integer>();
	}

	/**
	 * Called when a parameter is found.
	 */
	@Override
	public void parameterFound(String id, String type, String value) {
		// Ignored for contact map.
	}

	/**
	 * outside can be null
	 */
	@Override
	public void foundCompartment(String id, String volume, String outside) {
		// DEBUG
		// System.out.println("Found compartment:\n\tID: " + id + "\n\tVolume: " +
		// volume + "\n\tOutside: " + outside);

		// Try and get the parent
		Compartment parent = m_model.getCompartments().getCompartment(outside);

		// Using the parent (which may be null), add the compartment to the table.
		m_model.addCompartment(new Compartment(id, parent));
	}

	/**
	 * This method is called from the reader when a molecule is found in the
	 * molecule types block. The object is ready to be added to the model as is.
	 */
	@Override
	public void foundMoleculeType(Molecule molecule) {
		int index = m_model.addMolecule(molecule);
		m_moleculeIDForName.put(molecule.getName(), index);
	}

	/**
	 * This method is called from the the reader when a molecule is ofund in the
	 * seed species block. These molecules _should_ already exist, but may not. So
	 * we need to check to see if we have it in the model, and add it if we don't.
	 * 
	 * If it was already there, then we need to merge the two.
	 */
	@Override
	public void foundMoleculeInSeedSpecies(Molecule molecule) {

		Molecule existingMolecule = null;

		if (m_moleculeIDForName.get(molecule.getName()) != null) {
			existingMolecule = m_model.getMolecules().get(
			    m_moleculeIDForName.get(molecule.getName()));
		}

		// If we have it.
		if (existingMolecule != null) {
			existingMolecule.mergeData(molecule);
		}
		// If we do not have it.
		else {
			// Add it to the model and to the lookup table
			int index = m_model.addMolecule(molecule);
			m_moleculeIDForName.put(molecule.getName(), index);
		}
	}

	/**
	 * This method is called when a bond is found in the seed species block.
	 * 
	 * It needs to add a bond object to the model.
	 * 
	 */
	@Override
	public void foundBondInSeedSpecies(String moleName1, String compName1,
	    int compID1, String state1, String moleName2, String compName2,
	    int compID2, String state2) {
		// The old contact map did not include edges that were found in the seed
		// species block.

		addBondToModel(moleName1, compName1, compID1, state1, moleName2, compName2,
		    compID2, state2);
	}

	/**
	 * Private convenience method that adds a bond to the model.
	 * 
	 * @param moleName1
	 * @param compName1
	 * @param compID1
	 * @param state1
	 * @param moleName2
	 * @param compName2
	 * @param compID2
	 * @param state2
	 * @return
	 */
	private int addBondToModel(String moleName1, String compName1, int compID1,
	    String state1, String moleName2, String compName2, int compID2,
	    String state2) {
		// Need ints for all of these strings
		int moleIndex1 = m_moleculeIDForName.get(moleName1);
		int compIndex1 = m_model.getMolecules().get(moleIndex1)
		    .getComponentIndex(compName1, compID1);
		int stateIndex1 = (m_model.getMolecules().get(moleIndex1).getComponents()
		    .get(compIndex1)).getStateIndex(state1);

		// System.out.println("State index 1: " + stateIndex1);

		// Need ints for all of these strings
		int moleIndex2 = m_moleculeIDForName.get(moleName2);
		int compIndex2 = m_model.getMolecules().get(moleIndex2)
		    .getComponentIndex(compName2, compID2);
		int stateIndex2 = (m_model.getMolecules().get(moleIndex2).getComponents()
		    .get(compIndex2)).getStateIndex(state2);

		// System.out.println("State index 2: " + stateIndex2);

		return m_model.addBond(new Bond(moleIndex1, compIndex1, stateIndex1,
		    moleIndex2, compIndex2, stateIndex2));
	}

	private int addMoleculeToModel(String moleName1, String moleName2,
	    String action) {
		// Need ints for all of these strings
		if (action == "Add") {
			return m_moleculeIDForName.get(moleName1);

		} else {
			// System.out.println("State index 1: " + stateIndex1);

			// Need ints for all of these strings
			return m_moleculeIDForName.get(moleName2);
		}
	}

	/**
	 * Called when a rule is found. The RuleData object contains all of the
	 * information that can be gleaned from the model. The rest must be looked up
	 * from the existing data structures.
	 */
	@Override
	public void foundRule(RuleData ruleData) {
		String label = ruleData.getLabel();
		if (label.endsWith("r")) {
			// Get the existing rule.
			Rule existingRule = m_model.getRuleWithLabel(label.substring(0,
			    label.length() - 1));

			// If it is a reverse rule
			if (existingRule != null) {
				// Get the rule, set it to bidirectional, and set the 2nd rate.
				existingRule.setBidirection(true);
				existingRule.setRate2(ruleData.getRate());

				// Done
				return;
			}
		}
		// Create a new rule.
		Rule rule = new Rule();

		// Set the label of the rule.
		rule.setLabel(ruleData.getLabel());

		// Set the expression for the rule.
		rule.setRuleID(ruleData.getRuleID());

		// Set the rate
		rule.setRate1(ruleData.getRate());

		// For each RulePatternDataObject
		for (RulePatternData rpd : ruleData.getReactantPatternData()) {
			// Create the RulePattern Object
			RulePattern rulePattern = new RulePattern();
			rule.addReactantPattern(rulePattern);

			// For each MoleculePatternData object
			for (MoleculePatternData mpd : rpd.getMoleculePatterns()) {
				// Create the MoleculePattern object based on the id
				// of the molecule.
				// System.out.println("moleculeIDForName.get(mpd.getName()): " +
				// (m_moleculeIDForName.get(mpd.getName()) == null ? "null" :
				// m_moleculeIDForName.get(mpd.getName())));
				int moleID = m_moleculeIDForName.get(mpd.getName());

				// Get a reference to the molecule itself for later use.
				Molecule mole = m_model.getMolecules().get(moleID);

				// Create a new MoleculePattern.
				MoleculePattern moleculePattern = new MoleculePattern(moleID);
				rulePattern.addMoleculePattern(moleculePattern);

				// For each ComponentData object.
				for (ComponentData cd : mpd.getComponentPatterns()) {
					// Get the state value and add it to the molecule
					if (cd.getState() != null) {
						mole.addStateToComponent(cd.getState(), cd.getComponent());
					}

					// Add the component data to the molecule pattern object.
					moleculePattern.addComponentPattern(mole.getComponentIndex(
					    cd.getComponent(), cd.getUniqueID()),
					    mole.getComponent(cd.getComponent(), cd.getUniqueID())
					        .getStateIndex(cd.getState()), 0);

					// cd.getWildCards); TODO There can be be 'wildcard' bonds that
					// may potentially be connected to anything. Right now I am not
					// doing anything about those.

				} // done adding components to moleculepatterns
			} // done with the molecule patterns
		} // done with reactant patterns

		// TODO Do something with the bonds?

		// For each RulePatternDataObject
		for (RulePatternData rpd : ruleData.getProductPatternData()) {
			// Create the RulePattern Object
			RulePattern rulePattern = new RulePattern();
			rule.addProductPattern(rulePattern);

			// For each MoleculePatternData object
			for (MoleculePatternData mpd : rpd.getMoleculePatterns()) {
				// Create the MoleculePattern object based on the id
				// of the molecule.
				int moleID = m_moleculeIDForName.get(mpd.getName());

				// Get a reference to the molecule itself for later use.
				Molecule mole = m_model.getMolecules().get(moleID);

				// Create a new MoleculePattern.
				MoleculePattern moleculePattern = new MoleculePattern(moleID);
				rulePattern.addMoleculePattern(moleculePattern);

				// For each ComponentData object.
				for (ComponentData cd : mpd.getComponentPatterns()) {
					// Get the state value and add it to the molecule
					if (cd.getState() != null) {
						mole.addStateToComponent(cd.getState(), cd.getComponent());
					}

					moleculePattern.addComponentPattern(mole.getComponentIndex(
					    cd.getComponent(), cd.getUniqueID()),
					    mole.getComponent(cd.getComponent(), cd.getUniqueID())
					        .getStateIndex(cd.getState()), 0);
					// cd.getWildCards); TODO There can be be 'wildcard' bonds that
					// may potentially be connected to anything. Right now I am not
					// doing anything about those.

				} // done adding components to moleculepatterns
			} // done with the molecule patterns
		} // Done with product patterns

		// Bond Actions
		// The bonds that we see here will not have been seen before.
		// All other bonds are in the seed species.

		// So, we need to add the bonds, get the index, and then set the
		// index value in the rule object.

		// For each bond
		for (BondActionData bad : ruleData.getBondActions()) {
			// Add it to the model and get the index.
			int index;
			if (bad.getAction().endsWith("Bond")) {
				index = addBondToModel(bad.getBondData().getSourceMol(), bad
				    .getBondData().getSourceComp(), bad.getBondData().getSourceID(),
				    bad.getBondData().getSourceState(), bad.getBondData()
				        .getTargetMol(), bad.getBondData().getTargetComp(), bad
				        .getBondData().getTargetID(), bad.getBondData()
				        .getTargetState());
			} else {
				index = addMoleculeToModel(bad.getBondData().getSourceMol(), bad
				    .getBondData().getTargetMol(), bad.getAction());
			}
			// Add the BondAction to the rule.
			rule.addAction(new Action(index, bad.getAction()));
		}

		m_model.addRule(rule);
	}

	// Nothing is done with the observables in the Contact Map

	@Override
	public void foundObservable(String observableID, String observableName,
	    String observableType) {

	}

	@Override
	public void foundObservablePattern(String observableID, String patternID) {

	}

	@Override
	public void foundObservablePatternMolecule(String observableID,
	    String patternID, String moleculeID, String moleculeName) {
	}

	@Override
	public void foundObservablePatternMoleculeComponent(String observableID,
	    String patternID, String moleculeID, String componentID,
	    String componentName) {

	}

	@Override
	public void foundObservablePatternMoleculeComponentState(String observableID,
	    String patternID, String moleculeID, String componentID,
	    String componentState) {

	}

	public ContactMapModel getCMapModel() {
		return m_model;
	}
}
