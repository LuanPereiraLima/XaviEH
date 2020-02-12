package ufc.br.xavieh.test;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public class TestFiles {
    @SuppressWarnings("unused")
	public static void main(String[] args) {

		String[] mutations = {"CBD", "CBI", "CBR", "CRE", "FBD", "PTL"};
        String[] programs = {"commons-math-3.6.1"};//"commons-math-3.6.1", "commons-io-2.6", "commons-lang-3.8.1", "jsoup-1.11.3"};

        String path = "/media/loopback/C4DAE5FEDAE5EC9C/Users/luan_/mutationsTests2/commons-math-3.6.1/TSD";
        //String path = "/media/loopback/C4DAE5FEDAE5EC9C/Users/luan_/mutationsTests2/";

        File f = new File(path);

        File files[] = f.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                System.out.println(name);
                System.out.println(dir.getName());
                System.out.println(dir.getPath());
                if (dir.isDirectory() && !name.contains(".") && Integer.parseInt(name)<1420){
                    System.out.println("é diretorio: "+name+" "+dir.getAbsolutePath());
                    return true;
                }
                return false;
            }
        });

        for (int i = 0; i < files.length; i++) {
            //System.out.println(files[i].getName());
            File filesI[] = files[i].listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir2, String name2) {
                    //System.out.println(name2+" "+dir2.getAbsolutePath());
                    try {
                        if (dir2.isDirectory() && name2.equals("target"))
                            return true;
                    }catch (Exception e){
                    }
                    return false;
                }
            });
            for (int j = 0; j < filesI.length; j++) {
                System.out.println(filesI[j].getName() + " " + filesI[j].getAbsolutePath());
                //System.out.println(filesI[j].delete());
                try {
                    FileUtils.deleteDirectory(filesI[j]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        /*
        for(int l=0; l < programs.length; l++) {
            for (int k = 0; k < mutations.length; k++) {
                File f = new File(path + programs[l] + "/"+ mutations[k]);

                if (!f.exists())
                    continue;

                File files[] = f.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        System.out.println(name);
                        System.out.println(dir.getName());
                        System.out.println(dir.getPath());
                        if (dir.isDirectory() && !name.contains(".")) {// && Integer.parseInt(name)<938)
                            System.out.println("é diretorio: "+name+" "+dir.getAbsolutePath());
                            return true;
                        }
                        return false;
                    }
                });

                for (int i = 0; i < files.length; i++) {
                    //System.out.println(files[i].getName());
                    File filesI[] = files[i].listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir2, String name2) {
                            //System.out.println(name2+" "+dir2.getAbsolutePath());
                            try {
                                if (dir2.isDirectory() && name2.equals("target"))
                                    return true;
                            }catch (Exception e){
                            }
                            return false;
                        }
                    });
                    for (int j = 0; j < filesI.length; j++) {
                        System.out.println(filesI[j].getName() + " " + filesI[j].getAbsolutePath());
                        //System.out.println(filesI[j].delete());
                        try {
                            FileUtils.deleteDirectory(filesI[j]);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }*/
    }
}
