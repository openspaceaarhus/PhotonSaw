package dk.osaa.psaw.web.handlers;

import lombok.extern.java.Log;
import dk.osaa.psaw.core.PhotonSaw;
import dk.osaa.psaw.machine.MoveVector;
import dk.osaa.psaw.web.api.AbstractJSONHandler;
import dk.osaa.psaw.web.api.JSONParameters;
import dk.osaa.psaw.web.api.JSONResult;

@Log
public class JogHandler extends AbstractJSONHandler {

	public JogHandler(PhotonSaw ps) {
		super(ps, "jog");
	}
	
	public JSONResult jog(JSONParameters param) {
		JSONResult res = new JSONResult();

		MoveVector speed = new MoveVector();
		
		if (param.getMap().get("x") != null) {
			speed.setAxis(0, Double.valueOf(param.getString("x")));
		}
		
		if (param.getMap().get("y") != null) {
			speed.setAxis(1, Double.valueOf(param.getString("y")));
		}
		
		if (param.getMap().get("z") != null) {
			speed.setAxis(2, Double.valueOf(param.getString("z")));
		}
		
		if (param.getMap().get("a") != null) {
			speed.setAxis(3, Double.valueOf(param.getString("a")));
		}
		
		ps.setJogSpeed(speed);
		
		res.put("Test", "Hest");		
		
		return res;
	} 

}
