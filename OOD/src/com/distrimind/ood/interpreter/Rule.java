/*
Copyright or © or Copr. Jason Mahdjoub (01/04/2013)

jason.mahdjoub@distri-mind.fr

This software (Object Oriented Database (OOD)) is a computer program 
whose purpose is to manage a local database with the object paradigm 
and the java langage 

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 */
package com.distrimind.ood.interpreter;

import java.util.regex.Pattern;

/**
 * 
 * @author Jason Mahdjoub
 * @version 1.0
 * @since OOD 2.0
 */
public enum Rule 
{
    
    TERME("^(<"+SymbolType.IDENTIFIER.name()+ ">|<"+SymbolType.NUMBER.name()+">|<"+SymbolType.PARAMETER.name()+">|\\(<EXPRESSION>\\))$"),
    OP_MUL("^(<"+SymbolType.MUL_OPETATOR.name()+ ">|<"+SymbolType.DIV_OPERATOR.name()+">)$"),
    OP_ADD("^(<"+SymbolType.ADD_OPERATOR.name()+ ">|<"+SymbolType.ADD_OPERATOR.name()+">)$"),
    FACTEUR("^(<"+TERME.name()+ ">|<"+TERME.name()+ "><"+OP_MUL.name()+ "><FACTEUR>)$"),
    EXPRESSION("^(<"+FACTEUR.name()+">|<"+FACTEUR.name()+"><"+OP_ADD.name()+ "><EXPRESSION>)$"),
    OP_COMP("^(<"+SymbolType.EQUAL_OPERATOR.name()+ ">|<"+SymbolType.LOWER_OPERATOR.name()+ ">|<"+SymbolType.LOWER_OR_EQUAL_OPERATOR.name()+ ">|<"+SymbolType.GREATER_OPERATOR.name()+">|<"+SymbolType.GREATER_OR_EQUAL_OPERATOR.name()+">)$"),
    COMPARE("^(<"+EXPRESSION.name()+"><"+OP_COMP.name()+"><"+EXPRESSION.name()+">|\\(<QUERY>\\))$)"),
    OP_CONDITION("^(<"+SymbolType.AND_CONDITION.name()+">|<"+SymbolType.OR_CONDITION.name()+">)$"),
    QUERY("^(<"+COMPARE.name()+">|<"+COMPARE.name()+"><"+OP_CONDITION.name()+"><QUERY>)$");
    
    
    
    
    private final Pattern pattern;
    private Rule(String regex)
    {
	if (regex==null)
	    throw new NullPointerException("regex");
	this.pattern=Pattern.compile(regex);
    }
    
    public boolean match(String backusNaurRule)
    {
	return pattern.matcher(backusNaurRule).matches();
    }
}
