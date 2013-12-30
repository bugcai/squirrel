package org.squirrelframework.foundation.fsm.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.squirrelframework.foundation.fsm.Action;
import org.squirrelframework.foundation.fsm.MvelScriptManager;
import org.squirrelframework.foundation.fsm.StateMachine;

class MvelActionImpl<T extends StateMachine<T, S, E, C>, S, E, C> implements Action<T, S, E, C> {
    
    private static final Logger logger = LoggerFactory.getLogger(MvelActionImpl.class);
    
    private final String mvelExpression;
    
    private final MvelScriptManager scriptManager;
    
    private final String name;
    
    MvelActionImpl(String script, MvelScriptManager scriptManager) {
        String[] arrays = StringUtils.split(script, MvelScriptManager.SEPARATOR_CHARS);
        if(arrays.length==2) {
            this.name = arrays[0].trim();
            this.mvelExpression = arrays[1].trim();
        } else {
            this.name = "_NoName_";
            this.mvelExpression = arrays[0].trim();
        }
        
        this.scriptManager = scriptManager;
        scriptManager.compile(mvelExpression);
    }
    
    @Override
    public void execute(S from, S to, E event, C context, T stateMachine) {
        try {
            Map<String, Object> variables = new HashMap<String, Object>();
            variables.put("from", from);
            variables.put("to", to);
            variables.put("event", event);
            variables.put("context", context);
            variables.put("stateMachine", stateMachine);
            scriptManager.eval(mvelExpression, variables, Void.class);
        } catch (RuntimeException e) {
            logger.error("Evaluate \""+mvelExpression+"\" failed, which caused by "+e.getCause().getMessage());
            throw e;
        }
    }
    
    @Override
    public String name() {
        return name;
    }
}