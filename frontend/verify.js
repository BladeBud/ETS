const mailInput = document.getElementById("mailInput");
const verifyButton = document.getElementById("verifyBtn");
const instructionText = document.getElementById("instructionText");
const errorText = document.getElementById("errorText");
const alreadySentText = document.getElementById("alreadySentText");
const alreadyVerifiedText = document.getElementById("alreadyVerifiedText");
const unexpectedErrorText = document.getElementById("unexpectedErrorText");
const floorPlan = document.getElementById("floorPlanDiv");
const arrow = document.getElementById("arrow");
const dropdownToggle= document.getElementById("dropdownDiv");
const pastOrderDiv = document.getElementById("pastOrders");

var tables = [];
var verticalTables = [1174, 1474, 284, 874, 484, 374, 174, 274];
var horizontalTables = [632, 732, 642, 742];

class Table{
    constructor(posx, posy, width, height, totalspaces, availablespaces, selectedspaces, id, apiid){
        this.posx = posx;
        this.posy = posy;
        this.width = width;
        this.height = height;
        this.totalspaces = totalspaces;
        this.availablespaces = availablespaces;
        this.selectedspaces = selectedspaces;
        this.id = id;
        this.apiid = apiid;
    }
}

function onLoad(){
    tables.push(new Table(0,0,0,0,0,0,0,0, "stani"));

    tables.push(new Table(7.6,15.346534653465346,4.4,2.6166902404526167,6,6,0, 112, "druhepatrovlevo-1"));
    tables.push(new Table(7.6,20.297029702970296,4.4,2.545968882602546,6,6,0, 212, "druhepatrovlevo-2"));
    tables.push(new Table(7.6,24.893917963224894,4.4,2.6166902404526167,6,6,0, 312, "druhepatrovlevo-3"));
    tables.push(new Table(7.6,29.632248939179632,4.4,2.6166902404526167,6,6,0, 412, "druhepatrovlevo-4"));
    tables.push(new Table(7.7,34.37057991513437,4.3,2.6874115983026874,6,6,0, 512, "druhepatrovlevo-5")); 
    tables.push(new Table(7.6,39.10891089108911,4.5,2.6874115983026874,6,6,0, 612, "druhepatrovlevo-6"));
    tables.push(new Table(7.6,43.91796322489392,4.4,2.545968882602546,6,6,0, 712, "druhepatrovlevo-7"));
    tables.push(new Table(7.6,48.58557284299858,4.4,2.545968882602546,6,6,0, 812, "druhepatrovlevo-8"));
    tables.push(new Table(7.6,53.32390381895332,4.4,2.6874115983026874,6,6,0, 912, "druhepatrovlevo-9"));
    tables.push(new Table(7.6,58.13295615275813,4.4,2.6874115983026874,6,6,0, 1012, "druhepatrovlevo-10"));
    tables.push(new Table(7.6,63.08345120226308,4.4,2.6874115983026874,6,6,0, 1112, "druhepatrovlevo-11"));
    tables.push(new Table(7.6,67.68033946251768,4.4,2.6874115983026874,6,6,0, 1212, "druhepatrovlevo-12"));

    tables.push(new Table(18.2,15.37482276495646,4.4,2.758132956152758,6,6,0, 122, "prvnipatrovlevo-1")); 
    tables.push(new Table(18.2,20.254596456611342,4.4,2.6166902404526167,6,6,0, 222, "prvnipatrovlevo-2")); 
    tables.push(new Table(18.2,24.92220607471601,4.5,2.6166902404526167,6,6,0, 322, "prvnipatrovlevo-3")); 
    tables.push(new Table(18.2,29.759547599134216,4.4,2.6874115983026874,6,6,0, 422, "prvnipatrovlevo-4")); 
    tables.push(new Table(18.2,34.35643585938881,4.5,2.758132956152758,6,6,0, 522, "prvnipatrovlevo-5")); 
    tables.push(new Table(18.2,39.09476683534355,4.5,2.6449794310660137,6,6,0, 622, "prvnipatrovlevo-6")); 
    tables.push(new Table(18.2,43.86138700191169,4.5,2.6874115983026874,6,6,0, 722, "prvnipatrovlevo-7")); 
    tables.push(new Table(18.5,48.557284731507465,4.5,2.6166902404526167,6,6,0, 822, "prvnipatrovlevo-8")); 
    tables.push(new Table(18.6,53.18246110325318,4.5,2.6874115983026874,6,6,0, 922, "prvnipatrovlevo-9")); 
    tables.push(new Table(18.6,58.345120226308346,4.4,2.6874115983026874,6,6,0, 1022, "prvnipatrovlevo-10")); 
    tables.push(new Table(18.5,62.828856040598474,4.6,2.6874115983026874,6,6,0, 1122, "prvnipatrovlevo-11")); 
    tables.push(new Table(18.5,67.63790837440328,4.5,2.6874115983026874,6,6,0, 1222, "prvnipatrovlevo-12")); 
    tables.push(new Table(18.6,72.47524666145465,4.5,2.6166902404526167,6,6,0, 1322, "prvnipatrovlevo-13")); 
    tables.push(new Table(18.5,76.95898247574478,4.4,2.6874115983026874,6,6,0, 1422, "prvnipatrovlevo-14")); 

    tables.push(new Table(29.4,20.1131537409112,4.4,2.6874115983026874,6,6,0, 132, "vlevoprizemi-1"));
    tables.push(new Table(29.4,24.99292743256608,4.5,2.6166902404526167,6,6,0, 232, "vlevoprizemi-2"));
    tables.push(new Table(29.4,29.801979766370888,4.5,2.6166902404526167,6,6,0, 332, "vlevoprizemi-3"));
    tables.push(new Table(29.4,34.42715721723888,4.4,2.6874115983026874,6,6,0, 432, "vlevoprizemi-4"));
    tables.push(new Table(29.4,39.09476683534355,4.4,2.6166902404526167,6,6,0, 532, "vlevoprizemi-5"));
    tables.push(new Table(29.3,53.36633706531228,4.5,2.6166902404526167,6,6,0, 832, "vlevoprizemi-8"));
    tables.push(new Table(29.4,58.06223479490806,4.5,2.758132956152758,6,6,0, 932, "vlevoprizemi-9"));
    tables.push(new Table(29.4,62.97029875629862,4.5,2.6166902404526167,6,6,0, 1032, "vlevoprizemi-10"));
    tables.push(new Table(29.5,67.77935109010343,4.4,2.6874115983026874,6,6,0, 1132, "vlevoprizemi-11"));
    tables.push(new Table(29.4,72.40452530360457,4.4,2.6874115983026874,6,6,0, 1232, "vlevoprizemi-12"));
    tables.push(new Table(29.4,77.10042519144493,4.4,2.6874115983026874,6,6,0, 1332, "vlevoprizemi-13"));

    tables.push(new Table(65.61999969482422,25.176803394625175,4.5,2.6874115983026874,6,6,0, 242, "vpravoprizemi-2"));
    tables.push(new Table(65.61999969482422,30.02828897478894,4.5,2.6874115983026874,6,6,0, 342, "vpravoprizemi-3"));
    tables.push(new Table(65.61999969482422,34.55445587719347,4.5,2.758132956152758,6,6,0, 442, "vpravoprizemi-4"));
    tables.push(new Table(65.61999969482422,39.26449766253481,4.4,2.6874115983026874,6,6,0, 542, "vpravoprizemi-5"));
    tables.push(new Table(65.31999969482422,54.130126866795216,4.4,2.6166902404526167,6,6,0, 842, "vpravoprizemi-8"));
    tables.push(new Table(65.41999969482421,58.16124426424925,4.4,2.6874115983026874,6,6,0, 942, "vpravoprizemi-9"));
    tables.push(new Table(65.41999969482421,63.01272984441301,4.4,2.6874115983026874,6,6,0, 1042, "vpravoprizemi-10"));
    tables.push(new Table(65.41999969482421,67.46817538896747,4.4,2.6591213285670086,6,6,0, 1142, "vpravoprizemi-11"));

    tables.push(new Table(76.30500030517578,15.530410615524442,4.5,2.6874115983026874,6,6,0, 152, "prvnipatrovpravo-1"));
    tables.push(new Table(76.50500030517578,20.19802023362911,4.5,2.6874115983026874,6,6,0, 252, "prvnipatrovpravo-2"));
    tables.push(new Table(76.60500030517578,24.936351209583847,4.4,2.6874115983026874,6,6,0, 352, "prvnipatrovpravo-3"));
    tables.push(new Table(76.40500030517578,29.745403543388658,4.6,2.6874115983026874,6,6,0, 452, "prvnipatrovpravo-4"));
    tables.push(new Table(76.50500030517578,34.413013161493325,4.4,2.6166902404526167,6,6,0, 552, "prvnipatrovpravo-5"));
    tables.push(new Table(76.50500030517578,39.26449766253481,4.5,2.6166902404526167,6,6,0, 652, "prvnipatrovpravo-6"));
    tables.push(new Table(76.50500030517578,43.833096732176,4.5,2.6166902404526167,6,6,0, 752, "prvnipatrovpravo-7"));
    tables.push(new Table(76.40500030517578,48.57142770813074,4.6,2.6874115983026874,6,6,0, 852, "prvnipatrovpravo-8"));
    tables.push(new Table(76.50500030517578,53.281470572594365,4.4,2.6874115983026874,6,6,0, 952, "prvnipatrovpravo-9"));
    tables.push(new Table(76.50500030517578,58.23196562209932,4.5,2.6166902404526167,6,6,0, 1052, "prvnipatrovpravo-10"));
    tables.push(new Table(76.40500030517578,62.98443957467733,4.5,2.6874115983026874,6,6,0, 1152, "prvnipatrovpravo-11"));
    tables.push(new Table(76.50500030517578,67.72277055063206,4.5,2.758132956152758,6,6,0, 1252, "prvnipatrovpravo-12"));

    tables.push(new Table(87.20500030517579,15.459689257674372,4.5,2.6166902404526167,6,6,0, 162, "druhepatrovpravo-1"));
    tables.push(new Table(87.20500030517579,20.26874159147918,4.5,2.6166902404526167,6,6,0, 262, "druhepatrovpravo-2"));
    tables.push(new Table(87.30500030517578,24.86562985173378,4.4,2.6874115983026874,6,6,0, 362, "druhepatrovpravo-3"));
    tables.push(new Table(87.30500030517578,29.674682185538586,4.4,2.758132956152758,6,6,0, 462, "druhepatrovpravo-4"));
    tables.push(new Table(87.40500030517578,34.342291803643256,4.4,2.758132956152758,6,6,0, 562, "druhepatrovpravo-5"));
    tables.push(new Table(87.40500030517578,39.123054946834664,4.4,2.758132956152758,6,6,0, 662, "druhepatrovpravo-6"));
    tables.push(new Table(87.40500030517578,43.90381809002608,4.5,2.6166902404526167,6,6,0, 762, "druhepatrovpravo-7"));
    tables.push(new Table(87.30500030517578,48.57142770813074,4.5,2.6874115983026874,6,6,0, 862, "druhepatrovpravo-8"));
    tables.push(new Table(87.40500030517578,53.281470572594365,4.5,2.6166902404526167,6,6,0, 962, "druhepatrovpravo-9"));
    tables.push(new Table(87.40500030517578,58.23196562209932,4.4,2.6166902404526167,6,6,0, 1062, "druhepatrovpravo-10"));
    tables.push(new Table(87.40500030517578,62.94200848656294,4.4,2.758132956152758,6,6,0, 1162, "druhepatrovpravo-11"));
    tables.push(new Table(87.50500030517578,67.652049192782,4.3,2.758132956152758,6,6,0, 1262, "druhepatrovpravo-12"));

    tables.push(new Table(37.6,1.8387553041018387,3.8,3.1824611032531824,6,6,0, 2782, "druhepatrovzadu-27"));
    tables.push(new Table(42.4,1.8387553041018387,3.8,3.1117397454031117,6,6,0, 2882, "druhepatrovzadu-28"));
    tables.push(new Table(47.7,1.8387553041018387,3.7,3.1824611032531824,6,6,0, 2982, "druhepatrovzadu-29"));
    tables.push(new Table(52.4,1.768033946251768,3.9,3.1824611032531824,6,6,0, 3082, "druhepatrovzadu-30"));
    tables.push(new Table(57.7,1.768033946251768,3.8,3.1824611032531824,6,6,0, 3182, "druhepatrovzadu-31"));

    tables.push(new Table(33.13500061035156,87.94436528962606,3.7,3.1117397454031117,6,6,0, 1432, "vlevoprizemi-14"));
    tables.push(new Table(38.23500061035156,87.94436528962606,3.7,3.1824611032531824,6,6,0, 1532, "vlevoprizemi-15"));
    tables.push(new Table(57.63500061035156,88.01508664747612,3.6,3.1117397454031117,6,6,0, 1342, "vpravoprizemi-13"));
    tables.push(new Table(62.53500061035156,87.94436528962606,3.8,3.1824611032531824,6,6,0, 1242, "vpravoprizemi-12"));
    tables.push(new Table(65.51999969482422,20.438472418670436,2.9,2.6166902404526167,4,4,0, 142, "vpravoprizemi-1"));
    tables.push(new Table(29.3,49.33521966785825,4.5,1.768033946251768,4,4,0, 732, "vlevoprizemi-7"));
    tables.push(new Table(29.5,43.96039647125287,4.5,1.8387553041018387,4,4,0, 632, "vlevoprizemi-6"));
    tables.push(new Table(65.51999969482422,44.073549996339615,4.4,1.8387553041018387,4,4,0, 642, "vpravoprizemi-6"));
    tables.push(new Table(65.51999969482422,49.561526718031736,4.4,1.8387553041018387,4,4,0, 742, "vpravoprizemi-7"));

    //Bez výhledu
    tables.push(new Table(1.6,64.49787835926449,4.3,2.6874115983026874,6,6,0, 2213, "druhepatrovlevo-22"));
    tables.push(new Table(1.5,61.1032531824611,4.5,2.6166902404526167,6,6,0, 2113, "druhepatrovlevo-21"));
    tables.push(new Table(1.5,57.42574257425742,4.5,2.758132956152758,6,6,0, 2013, "druhepatrovlevo-20"));
    tables.push(new Table(1.6,53.677510608203676,4.4,2.758132956152758,6,6,0, 1913, "druhepatrovlevo-19"));
    tables.push(new Table(1.6,50.07072135785007,4.4,2.6166902404526167,6,6,0, 1813, "druhepatrovlevo-18"));
    tables.push(new Table(1.6,46.39321074964639,4.4,2.545968882602546,6,6,0, 1713, "druhepatrovlevo-17"));
    tables.push(new Table(1.6,42.57425742574257,4.4,2.545968882602546,6,6,0, 1613, "druhepatrovlevo-16"));
    tables.push(new Table(1.5,38.8967468175389,4.5,2.6166902404526167,6,6,0, 1513, "druhepatrovlevo-15"));
    tables.push(new Table(1.5,35.36067892503536,4.6,2.6166902404526167,6,6,0, 1413, "druhepatrovlevo-14"));
    tables.push(new Table(1.7,31.895332390381895,4.4,2.6874115983026874,6,6,0, 1313, "druhepatrovlevo-13"));

    tables.push(new Table(12.4,78.3734096327462,4.4,2.758132956152758,6,6,0, 2923, "prvnipatrovlevo-29")); 
    tables.push(new Table(12.4,75.19094852949301,4.4,2.6166902404526167,6,6,0, 2823, "prvnipatrovlevo-28")); 
    tables.push(new Table(12.4,71.93776606838976,4.4,2.6874115983026874,6,6,0, 2723, "prvnipatrovlevo-27")); 
    tables.push(new Table(12.4,68.75530496513657,4.4,2.6166902404526167,6,6,0, 2623, "prvnipatrovlevo-26")); 
    tables.push(new Table(12.4,65.54455359214772,4.4,2.6166902404526167,6,6,0, 2523, "prvnipatrovlevo-25")); 
    tables.push(new Table(12.3,62.36209248889453,4.5,2.6874115983026874,6,6,0, 2423, "prvnipatrovlevo-24")); 
    tables.push(new Table(12.4,59.10891002779128,4.4,2.6874115983026874,6,6,0, 2323, "prvnipatrovlevo-23")); 
    tables.push(new Table(12.4,55.82744161344148,4.5,2.545968882602546,6,6,0, 2223, "prvnipatrovlevo-22")); 
    tables.push(new Table(12.4,52.29137372093794,4.4,2.6166902404526167,6,6,0, 2123, "prvnipatrovlevo-21")); 
    tables.push(new Table(12.4,48.656294200848656,4.5,2.545968882602546,6,6,0, 2023, "prvnipatrovlevo-20")); 
    tables.push(new Table(12.4,45.04950495049505,4.4,2.6874115983026874,6,6,0, 1923, "prvnipatrovlevo-19")); 
    tables.push(new Table(12.4,41.485148946500395,4.4,2.6874115983026874,6,6,0, 1823, "prvnipatrovlevo-18")); 
    tables.push(new Table(12.4,37.10042475979601,4.4,2.6166902404526167,6,6,0, 1723, "prvnipatrovlevo-17")); 
    tables.push(new Table(12.3,32.758133819450585,4.4,2.6166902404526167,6,6,0, 1623, "prvnipatrovlevo-16")); 
    tables.push(new Table(12.4,28.161245559195986,4.3,2.6874115983026874,6,6,0, 1523, "prvnipatrovlevo-15"));

    tables.push(new Table(23.2,79.21734800433168,4.5,2.758132956152758,6,6,0, 2933, "vlevoprizemi-29"));
    tables.push(new Table(23.2,75.61055875397807,4.5,2.6166902404526167,6,6,0, 2833, "vlevoprizemi-28"));
    tables.push(new Table(23.2,72.21593357717468,4.5,2.6874115983026874,6,6,0, 2733, "vlevoprizemi-27"));
    tables.push(new Table(23.2,68.93446516282488,4.5,2.758132956152758,6,6,0, 2633, "vlevoprizemi-26"));
    tables.push(new Table(23.2,65.75200405957169,4.4,2.6166902404526167,6,6,0, 2533, "vlevoprizemi-25"));
    tables.push(new Table(23.2,62.56954295631851,4.4,2.6874115983026874,6,6,0, 2433, "vlevoprizemi-24"));
    tables.push(new Table(23.2,59.35879158332965,4.5,2.6874115983026874,6,6,0, 2333, "vlevoprizemi-23"));
    tables.push(new Table(23.2,56.247051837926534,4.5,2.6166902404526167,6,6,0, 2233, "vlevoprizemi-22"));
    tables.push(new Table(23.2,52.993869376823284,4.5,2.6166902404526167,6,6,0, 2133, "vlevoprizemi-21"));
    tables.push(new Table(23.2,47.44931751127122,4.5,2.6874115983026874,6,6,0, 2033, "vlevoprizemi-20"));
    tables.push(new Table(23.2,42.75341762343087,4.5,2.758132956152758,6,6,0, 1933, "vlevoprizemi-19"));
    tables.push(new Table(23.3,37.98679853598502,4.4,2.6874115983026874,6,6,0, 1833, "vlevoprizemi-18"));
    tables.push(new Table(23.2,33.319188917880346,4.5,2.758132956152758,6,6,0, 1733, "vlevoprizemi-17"));
    tables.push(new Table(23.2,28.623291188284565,4.4,2.6166902404526167,6,6,0, 1633, "vlevoprizemi-16"));

    tables.push(new Table(71.81999969482422,65.74257253083009,4.4,2.6874115983026874,6,6,0, 2543, "vpravoprizemi-25"));
    tables.push(new Table(71.81999969482422,62.560111427576906,4.4,2.6874115983026874,6,6,0, 2443, "vpravoprizemi-24"));
    tables.push(new Table(71.91999969482421,59.306928966473656,4.3,2.758132956152758,6,6,0, 2343, "vpravoprizemi-23"));
    tables.push(new Table(71.91999969482421,56.19518922107054,4.3,2.6874115983026874,6,6,0, 2243, "vpravoprizemi-22"));
    tables.push(new Table(71.91999969482421,53.04101838755304,4.3,2.6166902404526167,6,6,0, 2143, "vpravoprizemi-21"));
    tables.push(new Table(71.81999969482422,49.575671852899575,4.4,2.6874115983026874,6,6,0, 2043, "vpravoprizemi-20"));
    tables.push(new Table(71.81999969482422,46.25176803394625,4.4,2.6874115983026874,6,6,0, 1943, "vpravoprizemi-19"));
    tables.push(new Table(71.81999969482422,42.786421499292786,4.4,2.6874115983026874,6,6,0, 1843, "vpravoprizemi-18"));
    tables.push(new Table(71.91999969482421,39.27864171828036,4.4,2.6166902404526167,6,6,0, 1743, "vpravoprizemi-17"));
    tables.push(new Table(71.81999969482422,35.601131110076686,4.4,2.545968882602546,6,6,0, 1643, "vpravoprizemi-16"));
    tables.push(new Table(71.81999969482422,31.45685910841363,4.4,2.6874115983026874,6,6,0, 1543, "vpravoprizemi-15"));
    tables.push(new Table(71.81999969482422,27.56718442665974,4.4,2.6874115983026874,6,6,0, 1443, "vpravoprizemi-14"));

    tables.push(new Table(82.60500030517578,67.86421326633221,4.5,2.6166902404526167,6,6,0, 2553, "prvnipatrovpravo-25"));
    tables.push(new Table(82.70500030517579,64.54030944737889,4.4,2.6874115983026874,6,6,0, 2453, "prvnipatrovpravo-24"));
    tables.push(new Table(82.70500030517579,61.28712698627563,4.4,2.758132956152758,6,6,0, 2353, "prvnipatrovpravo-23"));
    tables.push(new Table(82.70500030517579,58.10466588302245,4.4,2.6874115983026874,6,6,0, 2253, "prvnipatrovpravo-22"));
    tables.push(new Table(82.60500030517578,54.92220477976927,4.5,2.6166902404526167,6,6,0, 2153, "prvnipatrovpravo-21"));
    tables.push(new Table(82.90500030517578,51.48514851485148,4.4,2.6874115983026874,6,6,0, 2053, "prvnipatrovpravo-20"));
    tables.push(new Table(82.80500030517578,48.09052333804809,4.4,2.6166902404526167,6,6,0, 1953, "prvnipatrovpravo-19"));
    tables.push(new Table(82.70500030517579,44.62517680339462,4.4,2.758132956152758,6,6,0, 1853, "prvnipatrovpravo-18"));
    tables.push(new Table(82.70500030517579,41.23055162659123,4.3,2.6874115983026874,6,6,0, 1753, "prvnipatrovpravo-17"));
    tables.push(new Table(82.70500030517579,37.93493591912902,4.4,2.6166902404526167,6,6,0, 1653, "prvnipatrovpravo-16"));
    tables.push(new Table(82.70500030517579,34.6110321001757,4.4,2.6874115983026874,6,6,0, 1553, "prvnipatrovpravo-15"));
    tables.push(new Table(82.60500030517578,31.287128281222373,4.5,2.6166902404526167,6,6,0, 1453, "prvnipatrovpravo-14"));
    tables.push(new Table(82.70500030517579,27.80763769082335,4.5,2.758132956152758,6,6,0, 1353, "prvnipatrovpravo-13"));

    tables.push(new Table(93.30500030517578,65.67185117298001,4.4,2.6166902404526167,6,6,0, 2463, "druhepatrovpravo-24"));
    tables.push(new Table(93.30500030517578,61.64073377552599,4.5,2.758132956152758,6,6,0, 2363, "druhepatrovpravo-23"));
    tables.push(new Table(93.40500030517578,58.175387240872524,4.3,2.6874115983026874,6,6,0, 2263, "druhepatrovpravo-22"));
    tables.push(new Table(93.30500030517578,54.31400282885431,4.5,2.758132956152758,6,6,0, 2163, "druhepatrovpravo-21"));
    tables.push(new Table(93.30500030517578,50.63649222065064,4.4,2.6874115983026874,6,6,0, 2063, "druhepatrovpravo-20"));
    tables.push(new Table(93.20500030517579,47.38330975954738,4.6,2.6874115983026874,6,6,0, 1963, "druhepatrovpravo-19"));
    tables.push(new Table(93.40500030517578,44.13012729844413,4.4,2.6166902404526167,6,6,0, 1863, "druhepatrovpravo-18"));
    tables.push(new Table(93.20500030517579,40.83451159098192,4.5,2.6874115983026874,6,6,0, 1763, "druhepatrovpravo-17"));
    tables.push(new Table(93.30500030517578,37.58132912987867,4.4,2.545968882602546,6,6,0, 1663, "druhepatrovpravo-16"));
    tables.push(new Table(93.20500030517579,34.25742531092534,4.5,2.6166902404526167,6,6,0, 1563, "druhepatrovpravo-15"));
    tables.push(new Table(93.20500030517579,30.65063606057174,4.4,2.758132956152758,6,6,0, 1463, "druhepatrovpravo-14"));
    tables.push(new Table(93.20500030517579,27.142856279559318,4.5,2.6874115983026874,6,6,0, 1363, "druhepatrovpravo-13"));
    
    //Lóže
    tables.push(new Table(65.42999877929688,71.93304814577439,3.9,6.294200848656294,6,6,0, 284, "prizemiloze-2"));
    tables.push(new Table(61.4,8.062234794908061,3.8,3.1117397454031117,6,6,0, 1074, "prvnipatroloze-10"));
    tables.push(new Table(56.4,7.991513437057991,3.8,3.1824611032531824,6,6,0, 1274, "prvnipatroloze-12"));
    tables.push(new Table(39.2,7.920792079207921,3.8,3.1117397454031117,6,6,0, 1374, "prvnipatroloze-13"));
    tables.push(new Table(34.3,7.85007072135785,3.7,3.1117397454031117,6,6,0, 974, "prvnipatroloze-9"));
    tables.push(new Table(64.33500061035156,91.55115453997966,3.7,3.1824611032531824,6,6,0, 674, "prvnipatroloze-6"));
    tables.push(new Table(59.53500061035156,91.55115453997966,3.8,3.1117397454031117,6,6,0, 474, "prvnipatroloze-4"));
    tables.push(new Table(36.435000610351565,91.6925972556798,3.7,3.1117397454031117,6,6,0, 574, "prvnipatroloze-5"));
    tables.push(new Table(31.235000610351562,91.62187589782974,3.9,3.1824611032531824,6,6,0, 774, "prvnipatroloze-7"));
    tables.push(new Table(87.42999877929688,72.07449086147454,3.9,6.223479490806223,6,6,0, 484, "druhepatroloze-4"));
    tables.push(new Table(76.52999877929688,72.00376950362447,3.7,6.223479490806223,12,12,0, 874, "prvnipatroloze-8"));
    tables.push(new Table(50.8,7.072135785007072,3.8,4.172560113154172,10,10,0, 1474, "prvnipatroloze-14"));
    tables.push(new Table(44.8,7.072135785007072,3.7,4.031117397454031,10,10,0, 1174, "prvnipatroloze-11"));
    tables.push(new Table(53.935000610351565,91.55115453997966,3.8,4.101838755304102,10,10,0, 274, "prvnipatroloze-2"));
    tables.push(new Table(47.73500061035156,91.6925972556798,3.8,5.162659123055162,10,10,0, 174, "prvnipatroloze-1"));
    tables.push(new Table(41.835000610351564,91.6925972556798,3.8,4.101838755304102,10,10,0, 374, "prvnipatroloze-3"));

    fetch(`link/api/order/misto?adresa=${Math.floor(Math.random() * 2147483647)}`, {
        method: "GET",
        //headers: {"Content-type": "application/json"}
    })
    .then(response => {
      // Check if the response is ok (status code 200–299)
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      // Parse the response as JSON
      return response.json();
    })
    .then(data => {
      // Handle the JSON data
      console.log(data);
        UpdateTableAvailable(data);
        CreateTableAnchors();
    })
    .catch(error => {
      // Handle any errors
      console.error('There was a problem with the fetch operation for available seats:', error);
      CreateTableAnchors();
    });
}

function UpdateTableAvailable(data){
    for(let i = 0; i < data.length; i++){
        var result = tables.findIndex(obj => {
            return obj.apiid == data[i].nazev;
          })
        
        tables[result].availablespaces = data[i].avaiablequantity;
        console.log(`${tables[result].availablespaces}`);
    }
}

function CreateTableAnchors(){
    console.log("creating anchors")
    for (let i = 0; i < tables.length; i++) {     //normal table
        element = tables[i];
        //console.log(element.id);
        _id = element.id.toString().substring(0, element.id.toString().length - 2);

        let anchor = document.createElement("a");
        anchor.href = "#";
        anchor.style = `left: ${element.posx}%; top: ${element.posy}%; width: ${element.width}%; height: ${element.height}%; display: block; position: absolute; margin: 0; font-family: "Lucida Console", monospace; cursor: default;`;
        anchor.class="tableArea";
        anchor.id = `a${element.id}`
        anchor.setAttribute('onclick',`event.preventDefault();`);

        if(verticalTables.includes(element.id)){
            anchor.innerHTML = `<div id="t${element.id}" style="height:100%; width:100%; display:flex; justify-content: center; align-items: center; float: right; color: white; flex-direction: column;"><font style="color: #B5EF8A;">${element.availablespaces}</font>${_id}</div>`;
            document.getElementById("floorPlan").appendChild(anchor);
            document.getElementById(`t${element.id}`).style.fontSize = `${document.getElementById(`a${element.id}`).offsetHeight/3.5}px`;
        }else if(horizontalTables.includes(element.id)){
            anchor.innerHTML = `<div id="t${element.id}" style="height:100%; width:100%; display:flex; justify-content: center; align-items: center; float: right; color: white; white-space: pre;"><font style="color: #B5EF8A;">${element.availablespaces}</font> ${_id}</div>`;
            document.getElementById("floorPlan").appendChild(anchor);
            document.getElementById(`t${element.id}`).style.fontSize = `${document.getElementById(`a${element.id}`).offsetHeight/2}px`;
        }else{
            anchor.innerHTML = `<div id="t${element.id}" style="height:100%; width:100%; display:flex; justify-content: center; align-items: center; float: right; color: white; flex-direction: column;"><font style="color: #B5EF8A;">${element.availablespaces}</font>${_id}</div>`;
            document.getElementById("floorPlan").appendChild(anchor);
            document.getElementById(`t${element.id}`).style.fontSize = `${document.getElementById(`a${element.id}`).offsetHeight/2.5}px`;
            document.getElementById(`t${element.id}`).style.lineHeight = `${document.getElementById(`a${element.id}`).offsetHeight/2.5}px`;
        }
    }
    floorPlan.style.display = "none";
}

function verifyEmail(){
    email = mailInput.value;
    errorText.style.display = "none";
    instructionText.style.display = "none";
    alreadySentText.style.display = "none";
    alreadyVerifiedText.style.display = "none";
    unexpectedErrorText.style.display = "none";
    pastOrderDiv.style.display = "none";

    if(!email.includes("@") || !email.includes(".") || email.includes("<") || email.includes(">") || email.includes(";")){   //* might need better conditions
        errorText.style.display = "block";
        return;
    }

    verifyButton.disabled = true;
    let countdown;
    const now = Date.now();
    const then = now + 60000; // 25 minutes in milliseconds
    clearInterval(countdown);
    countdown = setInterval(() => {
        const secondsLeft = Math.round((then - Date.now()) / 1000);
        if (secondsLeft <= 0) {
            clearInterval(countdown);
            verifyButton.disabled = false;
            verifyButton.innerText = "Ověřit";
            return;
        }

        const seconds = secondsLeft % 60;
        verifyButton.innerText = `${seconds}s`;
    }, 1000);
    
    fetch('link/api/ticket/login-register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ email: email.toString() }),
    })
    .then(response => {
        // Check the status code
        if (response.status == 200) {  //Verification sent / Already verified
            console.warn('200: Email sent');
            return response.text().then(text => { //List of orders of given customer / "Verification email sent."
                try {
                    // Try parsing text as JSON
                    return JSON.parse(text);
                } catch (e) {
                    // If parsing fails, return the text as-is
                    return text;
                }
            });
        } else if (response.status == 403) {   //Email sent but not verified
            alreadySentText.style.display = "block";
            console.warn('Request was forbidden (403) - Please verify your email.');
        } else {
            unexpectedErrorText.style.display = "block";
            console.error(`Unexpected status code: ${response.status}`);
        }
    })
    .then(data => {
        // Process the data if the response was successful
        console.log(data);
        if(data){
            if(typeof(data) == "string"){
                console.warn("Received plain text");
                instructionText.style.display = "block";
            }else{
                console.warn("Received JSON past orders");
                console.log(JSON.stringify(data, null, 2));
                //!showPastOrders(data);
                alreadyVerifiedText.style.display = "block";
            }
        }else{
            console.error("Invalid data");
        }
    })
    .catch(error => {
        unexpectedErrorText.style.display = "block";
        console.error('Error:', error);
    });
}

function showPastOrders(data){
    pastOrderDiv.innerHTML = JSON.stringify(data, null, 2);
    div = document.createElement("div");
    div.style = "background-color: #222222; border-radius: 5px;";
    div.id = `sumDiv0`;
    div.innerHTML = `
    <span id="sumType0" style="width: 30%; display: inline-block; vertical-align: middle;">Místo na stání</span>
    <span id="sumLocation0" style="width: 30%; display: inline-block; vertical-align: middle;"></span>
    <span id="sumSelected0" style="width: 10%; display: inline-block; vertical-align: middle;">${tables[0].selectedspaces}x</span>
    <span id="sumPrice0" style="width: 15%; display: inline-block; vertical-align: middle;">${tables[0].selectedspaces*300} Kč</span>
    <span style="width: 10%; display: inline-block; vertical-align: middle;"><button type="button" onclick="StandingCancel();" style="width: 100%; height: 100%; float: right; color: white; background-color: transparent; border: 1px solid; border-radius: 5px; padding: 3px; cursor: pointer;">Zrušit</button></span>
    `
    document.getElementById("summaryDiv").appendChild(div);
}

function toggleContent() {
    if (floorPlan.style.display == "none") {
        floorPlan.style.display = "block";
        arrow.classList.add("arrowRotated");
    } else {
        floorPlan.style.display = "none";
        arrow.classList.remove("arrowRotated");
    }
}

verifyButton.addEventListener("click", event => verifyEmail());

mailInput.addEventListener("keydown", event => {
    if(event.key === "Enter"){
        verifyEmail();
    }
})

dropdownToggle.addEventListener("click", event => toggleContent());