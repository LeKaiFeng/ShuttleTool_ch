package galaxis.lee.control.device;



import galaxis.lee.error.ErrorManager;

import java.util.Arrays;

public class ShuttleInsideState {

	public Boolean getIs_lowPower() {
		return is_lowPower;
	}

	public byte[] getErrormap() {
		return errormap;
	}

	public int getState() {
		return state;
	}

	public Boolean getIs_error() {
		return is_error;
	}

	public int getWorkmode() {
		return workmode;
	}

	public void setIs_lowPower(Boolean is_lowPower) {
		this.is_lowPower = is_lowPower;
	}

	public void setErrormap(byte[] receive, int receive_length) {
		for (int i = 0; i < error_length; i++) {
			errormap[i] = receive[i + receive_length];
		}
		this.generateErrorMessage();
	}

	public void setState(int state) {
		this.state = state;
	}

	public void setIs_error(Boolean is_error) {
		this.is_error = is_error;
	}

	public void setWorkmode(int workmode) {
		this.workmode = workmode;
		if (!errorMessage.equals("")) {
			// ErrorManager.addErrorMessage(Error.ERROR_FLASH,
			// "<li>Flash "+car_number+" error: </li>"+errorMessage);
			ErrorManager.addErrorMessage(ErrorManager.FLASH, car_number,
					errorMessage, error_level, error_pos, error_dir, error_dis,
					error_action);
		}
	}

	protected int error_level = 0;
	protected int error_pos = 0;
	protected int error_dir = 0;
	protected int error_dis = 0;
	protected int error_action = 0;

	public void setActionForErrorRecord(int level, int pos, int dir, int dis,
			int action) {
		this.error_level = level;
		this.error_pos = pos;
		this.error_dir = dir;
		this.error_dis = dis;
		this.error_action = action;
	}

	protected Boolean is_lowPower;
	protected byte[] errormap;
	protected int state;
	protected Boolean is_error;
	protected int workmode;
	protected int recover_mode;
	protected int error_length;
	protected int car_number;
	protected String errorMessage;
	protected int error_type;
	protected int resume_level;
	protected int resume_pos;
	protected int errorCode;

	public int getError_type() {
		return error_type;
	}

	public void refreshPosition() {
		// flashinsidestateservice.getResumePosition(car_number, this);
	}

	public int getResume_level() {
		return resume_level;
	}

	public int getResume_pos() {
		return resume_pos;
	}

	public void setError_type(int error_type) {
		this.error_type = error_type;
	}

	public void setResume_level(int resume_level) {
		this.resume_level = resume_level;
	}

	public void setResume_pos(int resume_pos) {
		this.resume_pos = resume_pos;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void resetResume() {
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
		// this.generateErrorMessageFromErrorCode();
		// flashinsidestateservice.updateErrorInfo(car_number, errorCode,
		// errorMessage);
	}


	public ShuttleInsideState(int e_length, int id) {

		error_length = e_length;
		state = ShuttleInsideState.READY_STATE;
		is_error = false;
		is_lowPower = false;
		errormap = new byte[error_length];
		Arrays.fill(errormap, (byte) 0);
		workmode = MANUALMODE;
		recover_mode = RECOVER_NORMAL;
		car_number = id;
		errorCode = 0;
		errorMessage = "";
		error_type = ERROR_NOERROR;
		resume_level = 0;
		resume_pos = 0;
	}

	public int getRecover_mode() {
		if (recover_mode == RECOVER_RESUME) {// || recover_mode ==
												// RECOVER_RESUME_BINEMPTY ||
												// recover_mode ==
												// RECOVER_RESUME_BINFULL) {
			return FlashReport.PROCESS_RESUME;
		} else if (recover_mode == RECOVER_CANCEL) {
			return FlashReport.PROCESS_CANCEL;
		} else {
			return FlashReport.PROCESS_ERROR;
		}
	}

	// public int getResume(){
	// return recover_mode;
	// }

	public static final byte AUTOMODE = 1;
	public static final byte SEMIAUTOMODE = 2;
	public static final byte MANUALMODE = 3;
	public static final byte SAUTOMODE = 4;
	public static final byte SSEMIAUTOMODE = 5;
	public static final byte SMANUALMODE = 6;

	public static final int READY_STATE = 0;
	public static final int PROCESS_STATE = 1;
	public static final int FINISH_STATE = 2;
	public static final int ERROR_STATE = 3;

	public static final int ERROR_NOERROR = 0;
	public static final int ERROR_NEED_LOCATION = 1;
	public static final int ERROR_OTHERS = 2;
	public static final int ERROR_BINEMPTY = 3;
	public static final int ERROR_BINFULL = 4;
	public static final int ERROR_POSSIABLE_EMPTY = 5;
	public static final int ERROR_POSSIABLE_FULL = 6;

	public static final int RECOVER_NORMAL = 0;
	public static final int RECOVER_RESUME = 1;
	public static final int RECOVER_CANCEL = 2;
	public static final int RECOVER_RESUME_REPOS = 3;

	protected void generateErrorMessage() {
		int i;
		errorMessage = "";
		Boolean is_error = false;
		for (i = 0; i < errormap.length; i++) {
			if (errormap[i] != 0) {
				is_error = true;
				break;
			}
		}
		if (!is_error) {
			return;
		}

		if (errormap[0] != 0) {
			if ((errormap[0] & 0x01) != 0) {
				errorMessage = errorMessage
						+ "<li>警告：X定位完成但是直行定位传感器未激活，代码1</li>";
			}
			if ((errormap[0] & 0x02) != 0) {
				errorMessage = errorMessage
						+ "<li>警告：Y定位完成但是直行定位传感器未激活，代码2</li>";
			}
			if ((errormap[0] & 0x04) != 0) {
				errorMessage = errorMessage + "<li>警告：电机未激活，代码3</li>";
			}
		}

		if (errormap[1] != 0) {
			if ((errormap[1] & 0x01) != 0) {
				errorMessage = errorMessage + "<li>警告：直行轮需要保养，代码9</li>";
			}
			if ((errormap[1] & 0x02) != 0) {
				errorMessage = errorMessage + "<li>警告：横行轮需要保养，代码10</li>";
			}
			if ((errormap[1] & 0x04) != 0) {
				errorMessage = errorMessage + "<li>警告：伸缩叉需要保养，代码11</li>";
			}
			if ((errormap[1] & 0x08) != 0) {
				errorMessage = errorMessage + "<li>警告：顶升机构需要保养，代码12</li>";
			}
			if ((errormap[1] & 0x10) != 0) {
				errorMessage = errorMessage + "<li>警告：指令格式错误，代码13</li>";
			}
			if ((errormap[1] & 0x20) != 0) {
				errorMessage = errorMessage + "<li>警告：错误的穿梭车编号，代码14</li>";
			}
			if ((errormap[1] & 0x40) != 0) {
				errorMessage = errorMessage + "<li>警告：取放货指令有冲突，代码15</li>";
			}
		}

		if (errormap[3] != 0) {
			if ((errormap[3] & 0x01) != 0) {
				errorMessage = errorMessage + "<li>警告：左取货指令冲突-左侧无货，代码25</li>";
			}
			if ((errormap[3] & 0x02) != 0) {
				errorMessage = errorMessage + "<li>警告：左取货指令冲突-车内有货，代码26</li>";
			}
			if ((errormap[3] & 0x04) != 0) {
				errorMessage = errorMessage + "<li>警告：右取货指令冲突-右侧无货，代码27</li>";
			}
			if ((errormap[3] & 0x08) != 0) {
				errorMessage = errorMessage + "<li>警告：右取货指令冲突-车内有货，代码28</li>";
			}
			if ((errormap[3] & 0x10) != 0) {
				errorMessage = errorMessage + "<li>警告：左放货指令冲突-左侧有货，代码29</li>";
			}
			if ((errormap[3] & 0x20) != 0) {
				errorMessage = errorMessage + "<li>警告：左放货指令冲突-车内无货，代码30</li>";
			}
			if ((errormap[3] & 0x40) != 0) {
				errorMessage = errorMessage + "<li>警告：右放货指令冲突-右侧有货，代码31</li>";
			}
			if ((errormap[3] & 0x80) != 0) {
				errorMessage = errorMessage + "<li>警告：右放货指令冲突-车内无货，代码32</li>";
			}
		}

		if (errormap[4] != 0) {
			if ((errormap[4] & 0x01) != 0) {
				errorMessage = errorMessage
						+ "<li>通信故障： Main_PLC-TSEND，代码33</li>";
			}
			if ((errormap[4] & 0x02) != 0) {
				errorMessage = errorMessage
						+ "<li>通信故障：Main_PLC TRCV，代码34</li>";
			}
			if ((errormap[4] & 0x04) != 0) {
				errorMessage = errorMessage + "<li>错误：提升轮未到下限位，代码35</li>";
			}
			if ((errormap[4] & 0x08) != 0) {
				errorMessage = errorMessage + "<li>错误：提升轮未到上限位，代码36</li>";
			}
			if ((errormap[4] & 0x10) != 0) {
				errorMessage = errorMessage + "<li>错误：伸缩叉不在原点，代码37</li>";
			}
			if ((errormap[4] & 0x20) != 0) {
				errorMessage = errorMessage + "<li>错误：左侧保护报警，代码38</li>";
			}
			if ((errormap[4] & 0x40) != 0) {
				errorMessage = errorMessage + "<li>错误：右侧保护报警，代码39</li>";
			}
			if ((errormap[4] & 0x80) != 0) {
				errorMessage = errorMessage + "<li>错误：急停被激活，代码40</li>";
			}
		}

		if (errormap[5] != 0) {
			if ((errormap[5] & 0x01) != 0) {
				errorMessage = errorMessage + "<li>错误：双侧保护报警，代码41</li>";
			}
			if ((errormap[5] & 0x02) != 0) {
				errorMessage = errorMessage + "<li>错误：无伸缩叉归零标记，代码42</li>";
			}
			if ((errormap[5] & 0x04) != 0) {
				errorMessage = errorMessage + "<li>错误：无finger归零标记，代码43</li>";
			}
			if ((errormap[5] & 0x08) != 0) {
				errorMessage = errorMessage + "<li>错误：伸缩叉驱动器故障，代码44</li>";
			}
			if ((errormap[5] & 0x10) != 0) {
				errorMessage = errorMessage + "<li>错误：顶升机构驱动器故障，代码45</li>";
			}
			if ((errormap[5] & 0x20) != 0) {
				errorMessage = errorMessage + "<li>错误：横行驱动器故障，代码46</li>";
			}
			if ((errormap[5] & 0x40) != 0) {
				errorMessage = errorMessage + "<li>错误：直行驱动器故障，代码47</li>";
			}
			if ((errormap[5] & 0x80) != 0) {
				errorMessage = errorMessage + "<li>超时：定位超时，代码48</li>";
			}
		}

		if (errormap[6] != 0) {
			if ((errormap[6] & 0x01) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码101：组态中的错误，代码49</li>";
			}
			if ((errormap[6] & 0x02) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码104：软限位开关的指定中存在错误，代码50</li>";
			}
			if ((errormap[6] & 0x04) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码105：组态模式（驱动装置/new java.awt.Font(name, 0, size));器），代码51</li>";
			}
			if ((errormap[6] & 0x08) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码106：驱动装置/执行器驱动程序中存在组态错误，代码52</li>";
			}
			if ((errormap[6] & 0x10) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码107：编码器组态错误，代码53</li>";
			}
			if ((errormap[6] & 0x20) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码108：编码器驱动程序组态错误，代码54</li>";
			}
			if ((errormap[6] & 0x40) != 0) {
				errorMessage = errorMessage + "<li>直行--错误代码109：组态错误，代码55</li>";
			}
			if ((errormap[6] & 0x80) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码110：正在进行内部调整组态，代码56</li>";
			}
		}

		if (errormap[7] != 0) {
			if ((errormap[7] & 0x01) != 0) {
				errorMessage = errorMessage + "<li>直行--错误代码201：内部错误，代码57</li>";
			}
			if ((errormap[7] & 0x02) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码202：内部组态错误，代码58</li>";
			}
			if ((errormap[7] & 0x04) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码203：内部错误。 请联系客户服务，代码59</li>";
			}
			if ((errormap[7] & 0x08) != 0) {
				errorMessage = errorMessage + "<li>直行--错误代码204：调试错误，代码60</li>";
			}
			if ((errormap[7] & 0x10) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码304：速度的限值为 0，代码61</li>";
			}
			if ((errormap[7] & 0x20) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码305：加速度/减速度的限值为 0，代码62</li>";
			}
			if ((errormap[7] & 0x40) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码306：加加速度的限值为 0，代码63</li>";
			}
			if ((errormap[7] & 0x80) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码307：达到了位置的数值范围，代码64</li>";
			}
		}

		if (errormap[8] != 0) {
			if ((errormap[8] & 0x01) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码308：超出了位置的数值范围，代码65</li>";
			}
			if ((errormap[8] & 0x02) != 0) {
				errorMessage = errorMessage + "<li>直行--错误代码321：轴未回原点，代码66</li>";
			}
			if ((errormap[8] & 0x04) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码322：未执行重新启动，代码67</li>";
			}
			if ((errormap[8] & 0x08) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码323：未执行 MC_Home，因为会导致超出数值范围，代码68</li>";
			}
			if ((errormap[8] & 0x10) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码341：回原点数据出错，代码69</li>";
			}
			if ((errormap[8] & 0x20) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码342：未找到参考凸轮/编码器零位标记，代码70</li>";
			}
			if ((errormap[8] & 0x40) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码401：访问逻辑地址时出错，代码71</li>";
			}
			if ((errormap[8] & 0x80) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码411：逻辑地址处的编码器损坏，代码72</li>";
			}
		}

		if (errormap[9] != 0) {
			if ((errormap[9] & 0x01) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码412：超出增量实际值的范围限制，代码73</li>";
			}
			if ((errormap[9] & 0x02) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码421：逻辑地址处的驱动装置/执行器损坏，代码74</li>";
			}
			if ((errormap[9] & 0x04) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码431：与设备的通信中断，代码75</li>";
			}
			if ((errormap[9] & 0x08) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码501：编程的速度受到限制，代码76</li>";
			}
			if ((errormap[9] & 0x10) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码502：编程的加速度/减速度受到限制，代码77</li>";
			}
			if ((errormap[9] & 0x20) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码503：编程的加加速度受到限制，代码78</li>";
			}
			if ((errormap[9] & 0x40) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码504：速度设定值监控处于激活状态，代码79</li>";
			}
			if ((errormap[9] & 0x80) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码521：超出跟踪误差监控的窗口，代码80</li>";
			}
		}

		if (errormap[10] != 0) {
			if ((errormap[10] & 0x01) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码522：超出跟踪误差监控的警告级别，代码81</li>";
			}
			if ((errormap[10] & 0x02) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码531：两个硬限位开关均激活，轴无法释放，代码82</li>";
			}
			if ((errormap[10] & 0x04) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码533：到达软限位开关，代码83</li>";
			}
			if ((errormap[10] & 0x08) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码534：通过软限位开关，代码84</li>";
			}
			if ((errormap[10] & 0x10) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码541：定位监控误差，代码85</li>";
			}
			if ((errormap[10] & 0x20) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码550：驱动装置执行自主移动，代码86</li>";
			}
			if ((errormap[10] & 0x40) != 0) {
				errorMessage = errorMessage
						+ "<li>直行--错误代码601：MC_GearIn.Master 参数指定的引导轴未组态或不可用，代码87</li>";
			}
		}

		if (errormap[14] != 0) {
			if ((errormap[14] & 0x01) != 0) {
				errorMessage = errorMessage + "<li>错误：伸缩叉初始化失败，代码113</li>";
			}
			if ((errormap[14] & 0x02) != 0) {
				errorMessage = errorMessage
						+ "<li>超时：伸缩叉初始化--伸缩叉回起点失败，代码114</li>";
			}
			if ((errormap[14] & 0x04) != 0) {
				errorMessage = errorMessage
						+ "<li>超时：伸缩叉初始化--伸缩叉触碰左侧间隙失败，代码115</li>";
			}
			if ((errormap[14] & 0x08) != 0) {
				errorMessage = errorMessage
						+ "<li>超时：伸缩叉初始化--伸缩叉触碰右侧间隙失败，代码116</li>";
			}
			if ((errormap[14] & 0x10) != 0) {
				errorMessage = errorMessage
						+ "<li>超时：伸缩叉初始化--伸缩叉回零点失败，代码117</li>";
			}
		}

		if (errormap[15] != 0) {
			if ((errormap[15] & 0x01) != 0) {
				errorMessage = errorMessage + "<li>错误：拨叉初始化失败，代码121</li>";
			}
			if ((errormap[15] & 0x02) != 0) {
				errorMessage = errorMessage
						+ "<li>超时：拨叉初始化--左侧拨叉上到位失败，代码122</li>";
			}
			if ((errormap[15] & 0x04) != 0) {
				errorMessage = errorMessage
						+ "<li>超时：拨叉初始化--右侧拨叉上到位失败，代码123</li>";
			}
			if ((errormap[15] & 0x08) != 0) {
				errorMessage = errorMessage
						+ "<li>超时：拨叉初始化--左侧拨叉下到位失败，代码124</li>";
			}
			if ((errormap[15] & 0x10) != 0) {
				errorMessage = errorMessage
						+ "<li>超时：拨叉初始化--右侧拨叉下到位失败，代码125</li>";
			}
			if ((errormap[15] & 0x80) != 0) {
				errorMessage = errorMessage + "<li>超时：取放货超时，代码128</li>";
			}
		}

		if (errormap[16] != 0) {
			if ((errormap[16] & 0x01) != 0) {
				errorMessage = errorMessage
						+ "<li>超时：右取1--双侧拨叉上到位失败，代码129</li>";
			}
			if ((errormap[16] & 0x02) != 0) {
				errorMessage = errorMessage
						+ "<li>超时：右取2--伸缩叉向右收回到指定位置失败，代码130</li>";
			}
			if ((errormap[16] & 0x04) != 0) {
				errorMessage = errorMessage
						+ "<li>超时：右取3--右侧拨叉下到位失败，代码131</li>";
			}
			if ((errormap[16] & 0x08) != 0) {
				errorMessage = errorMessage
						+ "<li>超时：右取4--伸缩叉向左收回到指定位置失败，代码132</li>";
			}
			if ((errormap[16] & 0x10) != 0) {
				errorMessage = errorMessage + "<li>超时：右取5--伸缩叉回原点失败，代码133</li>";
			}
		}

		if (errormap[17] != 0) {
			if ((errormap[17] & 0x01) != 0) {
				errorMessage = errorMessage
						+ "<li>超时：左取1--双侧拨叉上到位失败，代码137</li>";
			}
			if ((errormap[17] & 0x02) != 0) {
				errorMessage = errorMessage
						+ "<li>超时：左取2--伸缩叉向左收回到指定位置失败，代码138</li>";
			}
			if ((errormap[17] & 0x04) != 0) {
				errorMessage = errorMessage
						+ "<li>超时：左取3--左侧拨叉下到位失败，代码139</li>";
			}
			if ((errormap[17] & 0x08) != 0) {
				errorMessage = errorMessage
						+ "<li>超时：左取4--伸缩叉向右收回到指定位置失败，代码140</li>";
			}
			if ((errormap[17] & 0x10) != 0) {
				errorMessage = errorMessage + "<li>超时：左取5--伸缩叉回原点失败，代码141</li>";
			}
		}

		if (errormap[18] != 0) {
			if ((errormap[18] & 0x01) != 0) {
				errorMessage = errorMessage
						+ "<li>超时：右放1--左侧拨叉下到位失败，代码145</li>";
			}
			if ((errormap[18] & 0x02) != 0) {
				errorMessage = errorMessage
						+ "<li>超时：右放2--伸缩叉向右伸出到指定位置失败，代码146</li>";
			}
			if ((errormap[18] & 0x04) != 0) {
				errorMessage = errorMessage
						+ "<li>超时：右放3--右侧拨叉上到位失败，代码147</li>";
			}
			if ((errormap[18] & 0x08) != 0) {
				errorMessage = errorMessage
						+ "<li>超时：右放4--伸缩叉向左收回到指定位置失败，代码148</li>";
			}
			if ((errormap[18] & 0x10) != 0) {
				errorMessage = errorMessage + "<li>超时：右放5--伸缩叉回零点失败，代码149</li>";
			}
		}

		if (errormap[19] != 0) {
			if ((errormap[19] & 0x01) != 0) {
				errorMessage = errorMessage
						+ "<li>超时：左放1--右侧拨叉下到位失败，代码153</li>";
			}
			if ((errormap[19] & 0x02) != 0) {
				errorMessage = errorMessage
						+ "<li>超时：左放2--伸缩叉向左伸出到指定位置失败，代码154</li>";
			}
			if ((errormap[19] & 0x04) != 0) {
				errorMessage = errorMessage
						+ "<li>超时：左放3--左侧拨叉上到位失败，代码155</li>";
			}
			if ((errormap[19] & 0x08) != 0) {
				errorMessage = errorMessage
						+ "<li>超时：左放4--伸缩叉向右收回到指定位置失败，代码156</li>";
			}
			if ((errormap[19] & 0x10) != 0) {
				errorMessage = errorMessage + "<li>超时：左放5--伸缩叉回零点失败，代码157</li>";
			}
		}

		if (errormap[20] != 0) {
			if ((errormap[20] & 0x01) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码101：组态中的错误，代码161</li>";
			}
			if ((errormap[20] & 0x02) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码104：软限位开关的指定中存在错误，代码162</li>";
			}
			if ((errormap[20] & 0x04) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码105：组态模式（驱动装置/执行器），代码163</li>";
			}
			if ((errormap[20] & 0x08) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码106：驱动装置/执行器驱动程序中存在组态错误，代码164</li>";
			}
			if ((errormap[20] & 0x10) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码107：编码器组态错误，代码165</li>";
			}
			if ((errormap[20] & 0x20) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码108：编码器驱动程序组态错误，代码166</li>";
			}
			if ((errormap[20] & 0x40) != 0) {
				errorMessage = errorMessage + "<li>横行--错误代码109：组态错误，代码167</li>";
			}
			if ((errormap[20] & 0x80) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码110：正在进行内部调整组态，代码168</li>";
			}
		}

		if (errormap[21] != 0) {
			if ((errormap[21] & 0x01) != 0) {
				errorMessage = errorMessage + "<li>横行--错误代码201：内部错误，代码169</li>";
			}
			if ((errormap[21] & 0x02) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码202：内部组态错误，代码170</li>";
			}
			if ((errormap[21] & 0x04) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码203：内部错误。 请联系客户服务，代码171</li>";
			}
			if ((errormap[21] & 0x08) != 0) {
				errorMessage = errorMessage + "<li>横行--错误代码204：调试错误，代码172</li>";
			}
			if ((errormap[21] & 0x10) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码304：速度的限值为 0，代码173</li>";
			}
			if ((errormap[21] & 0x20) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码305：加速度/减速度的限值为 0，代码174</li>";
			}
			if ((errormap[21] & 0x40) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码306：加加速度的限值为 0，代码175</li>";
			}
			if ((errormap[21] & 0x80) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码307：达到了位置的数值范围，代码176</li>";
			}
		}

		if (errormap[22] != 0) {
			if ((errormap[22] & 0x01) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码308：超出了位置的数值范围，代码177</li>";
			}
			if ((errormap[22] & 0x02) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码321：轴未回原点，代码178</li>";
			}
			if ((errormap[22] & 0x04) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码322：未执行重新启动，代码179</li>";
			}
			if ((errormap[22] & 0x08) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码323：未执行 MC_Home，因为会导致超出数值范围，代码180</li>";
			}
			if ((errormap[22] & 0x10) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码341：回原点数据出错，代码181</li>";
			}
			if ((errormap[22] & 0x20) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码342：未找到参考凸轮/编码器零位标记，代码182</li>";
			}
			if ((errormap[22] & 0x40) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码401：访问逻辑地址时出错，代码183</li>";
			}
			if ((errormap[22] & 0x80) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码411：逻辑地址处的编码器损坏，代码184</li>";
			}
		}

		if (errormap[23] != 0) {
			if ((errormap[23] & 0x01) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码412：超出增量实际值的范围限制，代码185</li>";
			}
			if ((errormap[23] & 0x02) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码421：逻辑地址处的驱动装置/执行器损坏，代码186</li>";
			}
			if ((errormap[23] & 0x04) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码431：与设备的通信中断，代码187</li>";
			}
			if ((errormap[23] & 0x08) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码501：编程的速度受到限制，代码188</li>";
			}
			if ((errormap[23] & 0x10) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码502：编程的加速度/减速度受到限制，代码189</li>";
			}
			if ((errormap[23] & 0x20) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码503：编程的加加速度受到限制，代码190</li>";
			}
			if ((errormap[23] & 0x40) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码504：速度设定值监控处于激活状态，代码191</li>";
			}
			if ((errormap[23] & 0x80) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码521：超出跟踪误差监控的窗口，代码192</li>";
			}
		}

		if (errormap[24] != 0) {
			if ((errormap[24] & 0x01) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码522：超出跟踪误差监控的警告级别，代码193</li>";
			}
			if ((errormap[24] & 0x02) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码531：两个硬限位开关均激活，轴无法释放，代码194</li>";
			}
			if ((errormap[24] & 0x04) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码533：到达软限位开关，代码195</li>";
			}
			if ((errormap[24] & 0x08) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码534：通过软限位开关，代码196</li>";
			}
			if ((errormap[24] & 0x10) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码541：定位监控误差，代码197</li>";
			}
			if ((errormap[24] & 0x20) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码550：驱动装置执行自主移动，代码198</li>";
			}
			if ((errormap[24] & 0x40) != 0) {
				errorMessage = errorMessage
						+ "<li>横行--错误代码601：MC_GearIn.Master 参数指定的引导轴未组态或不可用，代码199</li>";
			}
		}

		if (errormap[25] != 0) {
			if ((errormap[25] & 0x01) != 0) {
				errorMessage = errorMessage + "<li>错误 直行 MC Power ，代码201</li>";
			}
			if ((errormap[25] & 0x02) != 0) {
				errorMessage = errorMessage
						+ "<li>错误 直行 MC MoveRelative，代码202</li>";
			}
			if ((errormap[25] & 0x04) != 0) {
				errorMessage = errorMessage + "<li>错误 直行 MC MoveJog，代码203</li>";
			}
		}

		if (errormap[26] != 0) {
			if ((errormap[26] & 0x01) != 0) {
				errorMessage = errorMessage + "<li>错误 直行 MC 8001 ，代码209</li>";
			}
			if ((errormap[26] & 0x02) != 0) {
				errorMessage = errorMessage + "<li>错误 直行 MC 8002 ，代码210</li>";
			}
			if ((errormap[26] & 0x04) != 0) {
				errorMessage = errorMessage + "<li>错误 直行 MC 8003 ，代码211</li>";
			}
			if ((errormap[26] & 0x08) != 0) {
				errorMessage = errorMessage + "<li>错误 直行 MC 8004 ，代码212</li>";
			}
			if ((errormap[26] & 0x10) != 0) {
				errorMessage = errorMessage + "<li>错误 直行 MC 8005 ，代码213</li>";
			}
			if ((errormap[26] & 0x20) != 0) {
				errorMessage = errorMessage + "<li>错误 直行 MC 8006 ，代码214</li>";
			}
			if ((errormap[26] & 0x40) != 0) {
				errorMessage = errorMessage + "<li>错误 直行 MC 8007 ，代码215</li>";
			}
			if ((errormap[26] & 0x80) != 0) {
				errorMessage = errorMessage + "<li>错误 直行 MC 8008 ，代码216</li>";
			}
		}

		if (errormap[27] != 0) {
			if ((errormap[27] & 0x01) != 0) {
				errorMessage = errorMessage + "<li>错误 直行 MC 8009 ，代码217</li>";
			}
			if ((errormap[27] & 0x02) != 0) {
				errorMessage = errorMessage + "<li>错误 直行 MC 800A ，代码218</li>";
			}
			if ((errormap[27] & 0x04) != 0) {
				errorMessage = errorMessage + "<li>错误 直行 MC 800B ，代码219</li>";
			}
			if ((errormap[27] & 0x08) != 0) {
				errorMessage = errorMessage + "<li>错误 直行 MC 800C ，代码220</li>";
			}
			if ((errormap[27] & 0x10) != 0) {
				errorMessage = errorMessage + "<li>错误 直行 MC 800D ，代码221</li>";
			}
			if ((errormap[27] & 0x20) != 0) {
				errorMessage = errorMessage + "<li>错误 直行 MC 800E ，代码222</li>";
			}
			if ((errormap[27] & 0x40) != 0) {
				errorMessage = errorMessage + "<li>错误 直行 MC 800F ，代码223</li>";
			}
			if ((errormap[27] & 0x80) != 0) {
				errorMessage = errorMessage + "<li>错误 直行 MC 8010 ，代码224</li>";
			}
		}

		if (errormap[28] != 0) {
			if ((errormap[28] & 0x01) != 0) {
				errorMessage = errorMessage + "<li>错误 直行 MC 8011 ，代码225</li>";
			}
			if ((errormap[28] & 0x02) != 0) {
				errorMessage = errorMessage + "<li>错误 直行 MC 8012 ，代码226</li>";
			}
			if ((errormap[28] & 0x04) != 0) {
				errorMessage = errorMessage + "<li>错误 直行 MC 8013 ，代码227</li>";
			}
			if ((errormap[28] & 0x08) != 0) {
				errorMessage = errorMessage + "<li>错误 直行 MC 8014 ，代码228</li>";
			}
			if ((errormap[28] & 0x10) != 0) {
				errorMessage = errorMessage + "<li>错误 直行 MC 8015 ，代码229</li>";
			}
			if ((errormap[28] & 0x20) != 0) {
				errorMessage = errorMessage + "<li>错误 直行 MC 8016 ，代码230</li>";
			}
			if ((errormap[28] & 0x40) != 0) {
				errorMessage = errorMessage + "<li>错误 直行 MC 8017 ，代码231</li>";
			}
			if ((errormap[28] & 0x80) != 0) {
				errorMessage = errorMessage + "<li>错误 直行 MC 8018 ，代码232</li>";
			}
		}

		if (errormap[29] != 0) {
			if ((errormap[29] & 0x01) != 0) {
				errorMessage = errorMessage + "<li>错误 直行 MC 8019 ，代码233</li>";
			}
			if ((errormap[29] & 0x02) != 0) {
				errorMessage = errorMessage + "<li>错误 直行 MC 8FFF ，代码234</li>";
			}
		}

		if (errormap[31] != 0) {
			if ((errormap[31] & 0x01) != 0) {
				errorMessage = errorMessage + "<li>错误 横行 MC Power ，代码249</li>";
			}
			if ((errormap[31] & 0x02) != 0) {
				errorMessage = errorMessage
						+ "<li>错误 横行 MC MoveRelative，代码250</li>";
			}
			if ((errormap[31] & 0x04) != 0) {
				errorMessage = errorMessage + "<li>错误 横行 MC MoveJog，代码251</li>";
			}
		}

		if (errormap[32] != 0) {
			if ((errormap[32] & 0x01) != 0) {
				errorMessage = errorMessage + "<li>错误 横行 MC 8001 ，代码257</li>";
			}
			if ((errormap[32] & 0x02) != 0) {
				errorMessage = errorMessage + "<li>错误 横行 MC 8002 ，代码258</li>";
			}
			if ((errormap[32] & 0x04) != 0) {
				errorMessage = errorMessage + "<li>错误 横行 MC 8003 ，代码259</li>";
			}
			if ((errormap[32] & 0x08) != 0) {
				errorMessage = errorMessage + "<li>错误 横行 MC 8004 ，代码260</li>";
			}
			if ((errormap[32] & 0x10) != 0) {
				errorMessage = errorMessage + "<li>错误 横行 MC 8005 ，代码261</li>";
			}
			if ((errormap[32] & 0x20) != 0) {
				errorMessage = errorMessage + "<li>错误 横行 MC 8006 ，代码262</li>";
			}
			if ((errormap[32] & 0x40) != 0) {
				errorMessage = errorMessage + "<li>错误 横行 MC 8007 ，代码263</li>";
			}
			if ((errormap[32] & 0x80) != 0) {
				errorMessage = errorMessage + "<li>错误 横行 MC 8008 ，代码264</li>";
			}
		}

		if (errormap[33] != 0) {
			if ((errormap[33] & 0x01) != 0) {
				errorMessage = errorMessage + "<li>错误 横行 MC 8009 ，代码265</li>";
			}
			if ((errormap[33] & 0x02) != 0) {
				errorMessage = errorMessage + "<li>错误 横行 MC 800A ，代码266</li>";
			}
			if ((errormap[33] & 0x04) != 0) {
				errorMessage = errorMessage + "<li>错误 横行 MC 800B ，代码267</li>";
			}
			if ((errormap[33] & 0x08) != 0) {
				errorMessage = errorMessage + "<li>错误 横行 MC 800C ，代码268</li>";
			}
			if ((errormap[33] & 0x10) != 0) {
				errorMessage = errorMessage + "<li>错误 横行 MC 800D ，代码269</li>";
			}
			if ((errormap[33] & 0x20) != 0) {
				errorMessage = errorMessage + "<li>错误 横行 MC 800E ，代码270</li>";
			}
			if ((errormap[33] & 0x40) != 0) {
				errorMessage = errorMessage + "<li>错误 横行 MC 800F ，代码271</li>";
			}
			if ((errormap[33] & 0x80) != 0) {
				errorMessage = errorMessage + "<li>错误 横行 MC 8010 ，代码272</li>";
			}
		}

		if (errormap[34] != 0) {
			if ((errormap[34] & 0x01) != 0) {
				errorMessage = errorMessage + "<li>错误 横行 MC 8011 ，代码273</li>";
			}
			if ((errormap[34] & 0x02) != 0) {
				errorMessage = errorMessage + "<li>错误 横行 MC 8012 ，代码274</li>";
			}
			if ((errormap[34] & 0x04) != 0) {
				errorMessage = errorMessage + "<li>错误 横行 MC 8013 ，代码275</li>";
			}
			if ((errormap[34] & 0x08) != 0) {
				errorMessage = errorMessage + "<li>错误 横行 MC 8014 ，代码276</li>";
			}
			if ((errormap[34] & 0x10) != 0) {
				errorMessage = errorMessage + "<li>错误 横行 MC 8015 ，代码277</li>";
			}
			if ((errormap[34] & 0x20) != 0) {
				errorMessage = errorMessage + "<li>错误 横行 MC 8016 ，代码278</li>";
			}
			if ((errormap[34] & 0x40) != 0) {
				errorMessage = errorMessage + "<li>错误 横行 MC 8017 ，代码279</li>";
			}
			if ((errormap[34] & 0x80) != 0) {
				errorMessage = errorMessage + "<li>错误 横行 MC 8018 ，代码280</li>";
			}
		}

		if (errormap[35] != 0) {
			if ((errormap[35] & 0x01) != 0) {
				errorMessage = errorMessage + "<li>错误 横行 MC 8019 ，代码281</li>";
			}
			if ((errormap[35] & 0x02) != 0) {
				errorMessage = errorMessage + "<li>错误 横行 MC 8FFF ，代码282</li>";
			}
		}

		if (errorMessage.equals("")) {
			errorMessage = errorMessage + "<li>错误：未知的系统底层异常，联系CNIT</li>";
		}

		// try {
		// errorMessage = new String(errorMessage.getBytes(),"utf-8");
		// } catch (UnsupportedEncodingException e) {
		// LogManager.getLogger().error(e.toString());
		// }
	}
}
