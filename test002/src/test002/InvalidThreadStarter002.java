/*******************************************************************************
 * Copyright (c) 2008, 2009 Ivan Egorov <egorich.3.04@gmail.com>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Ivan Egorov <egorich.3.04@gmail.com>
 *******************************************************************************/

package test002;

import test001.ValidThreadMarker;
import test001.ValidThreadMarker3;

public class InvalidThreadStarter002 implements ValidThreadMarker, ValidThreadMarker3, Runnable {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
