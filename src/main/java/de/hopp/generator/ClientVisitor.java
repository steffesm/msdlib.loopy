package de.hopp.generator;

import static de.hopp.generator.model.Model.*;
import katja.common.NE;
import de.hopp.generator.board.*;
import de.hopp.generator.board.Board.Visitor;
import de.hopp.generator.model.MAttribute;
import de.hopp.generator.model.MClass;
import de.hopp.generator.model.MFile;
import de.hopp.generator.model.MMethod;

public class ClientVisitor extends Visitor<NE> {

    private Configuration config;
    
    private MFile file;
    private MFile comps;
    private MClass comp;
    private MMethod init;
//    private MMethod clean;
    private MMethod main;
    
    public ClientVisitor(Configuration config) {
        this.config = config;
        
        // setup basic methods
        file  = MFile("name", MDefinitions(), MStructs(), MEnums(), MAttributes(), MMethods(), MClasses());
        comps = MFile("components", MDefinitions(), MStructs(), MEnums(), MAttributes(), MMethods(), MClasses());
        init  = MMethod(MDocumentation(Strings()), MModifiers(), MType("int"), "init", 
                MParameters(), MCode(Strings("")));
//        clean = MMethod(MDocumentation(Strings()), MModifiers(), MType("int"), "cleanup", 
//                MParameters(), MCode(Strings(""), MInclude("platform.h", QUOTES())));
        main  = MMethod(MDocumentation(Strings()), MModifiers(), MType("int"), "main", 
                MParameters(), MCode(Strings("", "// initialize board components", "init();")));
    }
    
    public MFile getFile() {
        return file;
    }
    public MFile getCompsFile() {
        return comps;
    }
    
    public void visit(Board board) {
        visit(board.components());
    }
    public void visit(Components comps) {
        for(Component c : comps) visit(c);
    }
    public void visit(UART term) {
        // TODO Auto-generated method stub
    }
    public void visit(ETHERNET_LITE term) {
        
//        int setup(int *Data_SocketFD) {
//            struct sockaddr_in stSockAddr;
//            int Res;
//            char *ip = "131.246.92.144";
//        //  char *ip = "192.168.1.10";
//
//            if(DEBUG) printf("setting up data socket @%s:%d ...", ip, NW_DATA_PORT);
//
//            if (-1 == *Data_SocketFD){ //|| -1 == Config_SocketFD){
//                printf(" failed to create socket");
//                exit(EXIT_FAILURE);
//            }
//
//            // Initialize Socket memory
//            memset(&stSockAddr, 0, sizeof(stSockAddr));
//
//            // Connect the Input Socket
//            stSockAddr.sin_family = AF_INET;
//            stSockAddr.sin_port = htons(NW_DATA_PORT);
//            Res = inet_pton(AF_INET, ip, &stSockAddr.sin_addr);
//
//            if (0 > Res){
//                printf(" error: first parameter is not a valid address family");
//                close(*Data_SocketFD);
//                exit(EXIT_FAILURE);
//            }
//            else if (0 == Res){
//                printf(" char string (second parameter does not contain valid ip address)");
//                close(*Data_SocketFD);
//                exit(EXIT_FAILURE);
//            }
//
//            if (-1 == connect(*Data_SocketFD, (struct sockaddr *)&stSockAddr, sizeof(stSockAddr))){
//                printf(" connect failed: %s (%d)", strerror(errno), errno);
////              printf("errorcode: %d", errno);
//                close(*Data_SocketFD);
//                exit(EXIT_FAILURE);
//            }
//
//            printf(" done\n");
//
//            return 0;
//        }
    }
    public void visit(ETHERNET term) {
        // TODO Auto-generated method stub
    }
    public void visit(PCIE term) {
        // TODO Auto-generated method stub
    }
    public void visit(LEDS term) {
        // TODO Auto-generated method stub
    }
    public void visit(SWITCHES term) {
        // TODO Auto-generated method stub
    }
    public void visit(BUTTONS term) {
        // TODO Auto-generated method stub
    }
    public void visit(VHDL vhdl) {
        // generate a class for the vhdl core
        visit(vhdl.core());
        
        // add an attribute for each used name
        for(String name : vhdl.names())
            comps = add(comps, MAttribute(MDocumentation(Strings()), MModifiers(PUBLIC()),
                MPointerType(MType(vhdl.core().file())), name, MCodeFragment("new " + vhdl.core().file() + " ()")));
    }
    public void visit(VHDLCore core) {
        comp = MClass(MDocumentation(Strings()), MModifiers(), core.file(), MTypes(MType("component")),
                MStructs(), MEnums(), MAttributes(), MMethods());
        visit(core.ports());
        comps = add(comps, comp);
    }
    public void visit(Ports ports) {
        for(Port p : ports) { visit(p); }
    }
    public void visit(IN in) {
        comp = add(comp, MAttribute(MDocumentation(Strings()), MModifiers(PUBLIC()), MPointerType(MType("in")),
                in.name(), MCodeFragment("new in()", MInclude("component.h", QUOTES()))));
    }
    public void visit(OUT out) {
        comp = add(comp, MAttribute(MDocumentation(Strings()), MModifiers(PUBLIC()), MPointerType(MType("out")),
                out.name(), MCodeFragment("new out()", MInclude("component.h", QUOTES()))));
    }
    public void visit(DUAL dual) {
        comp = add(comp, MAttribute(MDocumentation(Strings()), MModifiers(PUBLIC()), MPointerType(MType("dual")),
                dual.name(), MCodeFragment("new dual()", MInclude("component.h", QUOTES()))));
    }

    public void visit(Integer term) { }
    public void visit(Strings term) { }
    public void visit(String term)  { }
    
    private static MFile add(MFile file, MClass c) {
        return file.replaceClasses(file.classes().add(c));
    }
    private static MFile add(MFile file, MAttribute a) {
        return file.replaceAttributes(file.attributes().add(a));
    }
    private static MClass add(MClass c, MAttribute a) {
        return c.replaceAttributes(c.attributes().add(a));
    }
//    private static MClass add(MClass c, MMethod m) {
//        return c.replaceMethods(c.methods().add(m));
//    }
}
