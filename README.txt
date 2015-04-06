HOWTO:
	Run jar file like this:

	java -jar decide.jar <TRAINING_DATA_PATH> <TEST_DATA_PATH>
	Argument1: path to training data.
	Argument2: path to testing data.

	(only absolute path is supported...)

Example: 
	java -jar decide.jar /Users/mizhou/Documents/data/B/trainProdIntro.binary.arff /Users/mizhou/Documents/data/B/testProdIntro.binary.arff


Result:
	once it is executed successfully, it will generate 3 files in the same path:

(1) *****_tree.txt 
Location: 
	in the same directory as training data, **** is the name of training data, in this example, it generates trainProdIntro_tree.txt in /Users/mizhou/Documents/data/B/
Description: 
	it generates a best decision tree using training data
	best decision tree means this decision tree is pruned based on best threshhold
	this program will tune this "threshhold" automatically so that we can get the highest accuracy during cross validation

	we also print out the best result in the first line of this file:

		highest accuracy = 0.95625, best threshhold = 0.30125720377310217

		Advertisement_budget <= 1.49
		|----Period <= 50.0
		|----|----Customer = Professional [1] out of {1x4}
		|----|----Customer = Student
		|----|----|----Service_type = Fund [1] out of {1x4}
		|----|----|----Service_type = Bank_Account [0] out of {0x3}
		|----|----Customer = Business [1] out of {1x15}
		|----|----Customer = Doctor [0] out of {0x5}
		|----Period > 50.0 [0] out of {1x5 0x57}
		Advertisement_budget > 1.49 [1] out of {1x67}




(2)*****_result.csv
Location: 
	in the same directory as test data, **** is the name of test data, in this example, it generates testProdIntro_result.csv in /Users/mizhou/Documents/data/B/
Description: 
	it will print out classification result for the test data like this:
	you can open it directly in Microsoft EXCEL to get a better display format

	Attribute names are printed on the first row, 
	classification result will be printed on the last column.
	Label placeholders in tesdata will be ignored. 

			Service_type,Customer,Monthly_fee,Advertisement_budget,Size,Promotion,Interest_rate,Period,Label,
			Fund,Student,0.75,0.93,Small,Web&Email,1,5,1
			Fund,Business,1.1,0.93,Small,Web&Email,1,65,0
			Loan,Other,2.17,3.07,Small,Full,1,89,1
			Mortgage,Business,1.2,1.17,Small,Web,4,10,1
			
(3) *****_crossValidationResult.csv
Location: 
	in the same directory as training data, **** is the name of training data, in this example, it generates trainProdSelection_crossValidationResult.csv in /Users/mizhou/Documents/data/B/
Description: 
	it will print out cross validation details, you can open it directly in Microsoft Excel to get a better display format
	Attribute names are printed on the first row,
	
	------------------------------cross validation on group: 0--------------------------							
							
	training data: 1 11 21 31 41 51 61 71 81 91 101 111 121 131 141 151 161 171 181 2 12 22 32 42 52 62 72 82 92 102 112 122 132 142 152 162 172 182 3 13 23 33 43 53 63 73 83 93 103 113 123 133 143 153 163 173 183 4 14 24 34 44 54 64 74 84 94 104 114 124 134 144 154 164 174 184 5 15 25 35 45 55 65 75 85 95 105 115 125 135 145 155 165 175 185 6 16 26 36 46 56 66 76 86 96 106 116 126 136 146 156 166 176 7 17 27 37 47 57 67 77 87 97 107 117 127 137 147 157 167 177 8 18 28 38 48 58 68 78 88 98 108 118 128 138 148 158 168 178 9 19 29 39 49 59 69 79 89 99 109 119 129 139 149 159 169 179 							
							
	test data : 0 10 20 30 40 50 60 70 80 90 100 110 120 130 140 150 160 170 180 							
							
	Type	LifeStyle	Vacation	eCredit	salary	property	label	classification
							
	student	spend>saving	6	40	13.62	3.2804	C1	C1
	student	spend>>saving	25	30	15.64	3.1282	C1	C4
	engineer	spend>saving	41	50	21	6.8701	C1	C4
	engineer	spend<<saving	39	87	19.95	4.0586	C1	C4
	librarian	spend>saving	5	11	14.6699	1.0147	C2	C2
	professor	spend>saving	10	9	20.5991	0.552	C2	C2
	professor	spend<saving	6	17	18.5944	0.79	C2	C2
	student	spend>saving	10	66	20.09	3.9933	C3	C3
	doctor	spend>saving	7	253	25.39	6.6154	C3	C3
	doctor	spend>saving	11	91	26.94	14.8164	C3	C3
	doctor	spend<saving	13	284	24.11	7.9947	C3	C3
	doctor	spend>saving	26	60	20.06	4.131	C4	C4
	professor	spend>>saving	44	44	21.51	2.6675	C4	C4
	librarian	spend>>saving	41	37	19.8	2.8195	C4	C4
	engineer	spend>>saving	50	55	20.82	3.0217	C4	C4
	professor	spend>saving	48	5	20.39	1.7536	C5	C5
	professor	spend>>saving	50	5	22.71	3.3358	C5	C5
	engineer	spend>saving	51	22	21.99	1.358	C5	C5
	engineer	spend>>saving	50	23	20.17	2.3291	C5	C5
	=======correct : 16	 wrong : 3 accuracy: 0.8421052631578947
	
	
	it also print the average accuracy for this cross validation at the end of the file:
	average accuracy of this cross validation is 0.861111111111111