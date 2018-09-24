#ifndef MYHEADER_APPMODCC
#define MYHEADER_APPMODCC
#include <mutex>

#include "appmod.h"

#include <cstddef>

#include "android-base/stringprintf.h"

#include "arch/context.h"
#include "art_method-inl.h"
#include "base/stringpiece.h"
#include "class_linker-inl.h"
#include "debugger.h"
#include "dex_file-inl.h"
#include "dex_file_annotations.h"
#include "dex_instruction.h"
#include "entrypoints/runtime_asm_entrypoints.h"
#include "gc/accounting/card_table-inl.h"
#include "interpreter/interpreter.h"
#include "jit/jit.h"
#include "jit/jit_code_cache.h"
#include "jit/profiling_info.h"
#include "jni_internal.h"
#include "mirror/class-inl.h"
#include "mirror/class_ext.h"
#include "mirror/executable.h"
#include "mirror/object_array-inl.h"
#include "mirror/object-inl.h"
#include "mirror/string.h"
#include "oat_file-inl.h"
#include "runtime_callbacks.h"
#include "scoped_thread_state_change-inl.h"
#include "vdex_file.h"
#include "well_known_classes.h"
#include <sstream>
#include <ctime>
#include <jni.h>
#include <sys/types.h>
#include <unistd.h>

#include <map>
#include <stdio.h>
#include <string>
#include <set>
#include <vector>
#include <list>
#include <stdlib.h>
#include <string.h>
#include <mutex>
#include <dirent.h>

#endif

using android::base::StringPrintf;



namespace appmod_ns{
    const int MAX_STR_LEN = 2000;
}
char *trim (char *s) {
    int l = strlen(s);
    while (l>0 && (s[l-1] =='\n' || s[l-1]=='\r')){
        s[l-1]=0;
        l=strlen(s);
    }
  return s;
}

bool str_ends_with(const char* st, const char* suffix){
    int l_st = strlen(st);
    int l_suffix = strlen(suffix);
    //printf("%s %s\n",st,suffix);
    if (l_suffix <=l_st){
        for (int i=0;i<l_suffix;i++){
            if (suffix[l_suffix-1-i] != st[l_st-1-i]){
                return false;
            }
        }
        return true;
    }else{
        return false;
    }
}

class Edge{
    private:
    std::string*  source;
    std::string* dest;
    std::string* label;

    public:
    
    Edge(const std::string & s , const std::string& d, const std::string& l){
        this->source = new std::string(s);
        this->dest = new std::string(d);
        this->label = new std::string(l);
    }

    std::string* get_source_node(){
        return this->source;
    }
    std::string* get_destination_node(){
        return this->dest;
    }
    std::string*  get_label(){
        return this->label;
    }

    ~Edge(){
        delete this->source;
        delete this->dest;
        delete this-> label;
    }

    std::string to_string(){
        return (*this->source + " "+*this->dest +" "+*this->label);
    }

};

class Automata{
    private:
    std::vector<std::string>* starting_nodes;
    std::vector<std::string>* ending_nodes;
    std::vector<Edge*>* transitions;
    std::map< std::string, std::list<Edge*>* >* adjacent_list;
    std::map< std::string, std::list<Edge*>* >* rev_adjacent_list;

    std::set<std::string>* vocabulary;
    std::set<std::string>* sensitive_APIs;

    // std::map< std::string, std::map<std::string, std::list<Edge*>*>*>* adjacent_map_list;

    int length_of_execution_queue;

    public:

    std::vector<std::string>* get_starting_nodes(){
        return this->starting_nodes;
    }
    std::vector<std::string>* get_ending_nodes(){
        return this->ending_nodes;
    }

    Automata(std::vector<std::string>& ss, std::vector<std::string>& es, std::vector<Edge*>& edges,std::vector<std::string>& interested_labels,std::vector<std::string>& triggers, int execution_queue_length){
        this->length_of_execution_queue = execution_queue_length;
        this->starting_nodes = new std::vector<std::string>(ss);
        this->ending_nodes = new std::vector<std::string>(es);
        this->transitions = new std::vector<Edge*>(edges);
        this->vocabulary = new std::set<std::string>(interested_labels.begin(),interested_labels.end());
        this->sensitive_APIs = new std::set<std::string>(triggers.begin(),triggers.end());

        this->adjacent_list = new std::map<std::string, std::list<Edge*>* >();
        this->rev_adjacent_list = new std::map<std::string,std::list<Edge*>* >();
        // this->state_label_mapping = new std::map< std::string, std::map<std::string, std::list<Edge*>*>*>();

        for (std::vector<Edge*>::iterator it= this->transitions->begin();it!=this->transitions->end();it++){
            Edge* the_edge = *it;
            this->vocabulary->insert(*the_edge->get_label());
          
            std::map< std::string, std::list<Edge*>*  >::iterator list_it =  this->adjacent_list->find(*the_edge->get_source_node());

            if (list_it!=this->adjacent_list->end()){
                list_it->second->push_back(the_edge);
            }else{
                std::list<Edge*>* l=new std::list<Edge*>();
                l->push_back(the_edge);
                (*this->adjacent_list)[(*the_edge->get_source_node())]=l;
            }
            
            std::map< std::string, std::list<Edge*>*  >::iterator rev_list_it =  this->rev_adjacent_list->find(*the_edge->get_destination_node());
            if (rev_list_it!=this->rev_adjacent_list->end()){
                rev_list_it->second->push_back(the_edge);
            }else{
                std::list<Edge*>* l=new std::list<Edge*>();
                l->push_back(the_edge);
                (*this->rev_adjacent_list)[(*the_edge->get_destination_node())]=l;
            }

           
            
        }
    }
    int get_execution_queue_length(){
        return this->length_of_execution_queue;
    }
    std::vector<Edge*>* get_transitions(){
        return this->transitions;
    }
    ~Automata(){
        delete this->starting_nodes;
        delete this->ending_nodes;
        delete this->vocabulary;
        delete this->sensitive_APIs;

        for (std::vector<Edge*>::iterator it= this->transitions->begin();it!=this->transitions->end();it++){
            delete* it;
        }
        delete this-> transitions;
        
        for ( std::map< std::string, std::list<Edge*>*  >::iterator it = this->adjacent_list->begin();it!=this->adjacent_list->end();it++){
            delete it->second;
        }

        for ( std::map< std::string, std::list<Edge*>*  >::iterator it = this->rev_adjacent_list->begin();it!=this->rev_adjacent_list->end();it++){
            delete it->second;
        }

        delete this->adjacent_list;
        delete this-> rev_adjacent_list;
        //printf("Finish descrtucting ...\n");
    }

    std::set<std::string>* get_vocabulary(){
        return this->vocabulary;
    }

    std::set<std::string>* get_sensitive_APIs(){
        return this->sensitive_APIs;
    }

    bool accept_trace(std::list<std::string>* trace,std::vector<std::string>* current_states){
        std::set<std::string> cur(current_states->begin(),current_states->end());
        
        for (std::list<std::string>::iterator method_it = trace->begin();method_it!=trace->end();method_it++){
            std::set<std::string> next;
            
            for (std::set<std::string>::iterator state_it = cur.begin();state_it!=cur.end();state_it++){
                std::string the_state = *state_it;
                std::map< std::string, std::list<Edge*>*  >::iterator edge_it = this->adjacent_list->find(the_state);
                if (edge_it!= this->adjacent_list->end()){
                    std::list<Edge*>* edge_lst = edge_it->second;
                    for (std::list<Edge*>::iterator e_it = edge_lst->begin();e_it!=edge_lst->end();e_it++){
                        Edge* the_edge = *e_it;
                        if (*the_edge->get_label() == *method_it && the_state == *the_edge->get_source_node()){
                            next.insert(*the_edge->get_destination_node());
                        }
                    }
                }
            }
            if (next.empty()){
                //printf("Reject!\n");
                return false;
            }
            cur.clear();
            cur.insert(next.begin(),next.end());
        }
        //printf("Accept!\n");
        return true;
    }

};

class BehaviorModelChecker{
    private:
        Automata* model=NULL;
        // std::set<std::string>* current_states;
        
        std::mutex mtx_queue;
        std::map<pid_t,std::list<std::string>*>* thread_to_executed_queue;

        char* pid_string=NULL;

    public:
        Automata* get_behavior_model(){
            return this->model;
        }
        BehaviorModelChecker(Automata* fsa, pid_t process_id){
            model=fsa;
            this->pid_string=new char[appmod_ns::MAX_STR_LEN];
            sprintf(this->pid_string, "%ld", (long) process_id);
            this->thread_to_executed_queue = new std::map<pid_t, std::list<std::string>*>();
        }
        const char* get_pid(){
            return this->pid_string;
        }
        void set_pid(pid_t process_id){
            std::lock_guard<std::mutex> lock(this->mtx_queue);
            sprintf(this->pid_string, "%ld", (long) process_id);
        }
        void reset(){
            std::lock_guard<std::mutex> lock(this->mtx_queue);
            this->thread_to_executed_queue->clear();
        }
        ~BehaviorModelChecker(){
            // delete this->current_states;
            for ( std::map<pid_t, std::list<std::string>* >::iterator  it = this->thread_to_executed_queue->begin();it!=this->thread_to_executed_queue->end();it++)
            {
                delete it->second;        
            }
            delete this->thread_to_executed_queue;
            delete[] this->pid_string;
            LOG(INFO) << StringPrintf("DUY_DEBUG: BehaviorModelChecker is terminated.");

        }

        /**
         * return TRUE means not malicious
         * return FALSE means malicious
         */
        bool add_executed_method(pid_t threadid, const char* method){
            std::string method_str(method);

            if (this->model->get_vocabulary()->count(method_str)==0){
                // printf("Ignore %s\n",method.c_str());
                return true;
            }
            // printf("Adding %s\n",std::string(method).c_str());
            std::list<std::string> seq;
            std::map<pid_t, std::list<std::string>* >::iterator  it;
            {
                std::lock_guard<std::mutex> lock(this->mtx_queue);
                it=this->thread_to_executed_queue->find(threadid);
            }
                
            std::list<std::string>* lst =NULL;
            if (it != this->thread_to_executed_queue->end()){
                lst = it->second;
            }else{
                lst = new std::list<std::string>();
                (*this->thread_to_executed_queue)[threadid]=lst;
            }
            lst->push_front(method_str);
            while ((int) lst->size()>this->model->get_execution_queue_length()){
                lst->pop_back();
            }
            seq.insert(seq.end(),lst->begin(),lst->end());
            
            if (this->model->get_sensitive_APIs()->count(method_str)>0)   {
                bool flag= this->model->accept_trace(&seq,this->model->get_starting_nodes());
                return flag;
            }else{
                return true;
            }
            
        }
      
};


class BehaviorModelCollection{
    private:
        const std::string MODEL_FILE_EXT=".txt";

        std::mutex mtx_mapping;
        std::map<std::string,BehaviorModelChecker*>* mapping;
        std::string* PATH_TO_MODEL_FOLDER;

        BehaviorModelChecker* parse_model(const char* file_name,pid_t process_id){                
            // LOG(INFO) << StringPrintf("DUY_DEBUG: try to parse \"%s\" [END]",file_name);
            FILE* f = fopen(file_name,"r");
            if (f==NULL){
                return NULL;
            }
            /*****************************************************/
            // LOG(INFO) << StringPrintf("DUY_DEBUG: reading \"%s\" [END]",file_name);
            char* the_line=new char[appmod_ns::MAX_STR_LEN];
            int num_startings=atoi(fgets(the_line,appmod_ns::MAX_STR_LEN,f));
            std::vector<std::string> starting_nodes(num_startings,"");
            for (int i=0;i < num_startings;i++){
                starting_nodes[i]=std::string(trim(fgets(the_line,appmod_ns::MAX_STR_LEN,f)));
            }
            /****************/
            int num_endings=atoi(fgets(the_line,appmod_ns::MAX_STR_LEN,f));
            std::vector<std::string> ending_nodes(num_endings,"");
            for (int i=0;i<num_endings;i++){
                ending_nodes[i]=std::string(trim(fgets(the_line,appmod_ns::MAX_STR_LEN,f)));
            }
            /****************/
            int num_label=atoi(fgets(the_line,appmod_ns::MAX_STR_LEN,f));
            std::vector<std::string> labels(num_label,"");
            for (int i=0;i<num_label;i++){
                labels[i]=std::string(trim(fgets(the_line,appmod_ns::MAX_STR_LEN,f)));
            }
            /****************/
            int sensitive_api_label_num=atoi(fgets(the_line,appmod_ns::MAX_STR_LEN,f));
            std::vector<std::string> importances(sensitive_api_label_num,"");
            for (int i=0;i<sensitive_api_label_num;i++){
                int id = atoi(fgets(the_line,appmod_ns::MAX_STR_LEN,f));
                importances[i]=labels[id];
            }
            /****************/
            int num_edges=atoi(fgets(the_line,appmod_ns::MAX_STR_LEN,f));
            std::vector<Edge*> edges(num_edges,NULL);

            char* source = new char[appmod_ns::MAX_STR_LEN];
            char* dest = new char[appmod_ns::MAX_STR_LEN];
            char* label = new char[appmod_ns::MAX_STR_LEN];

            for (int i=0;i<num_edges;i++){
                char*st = trim(fgets(the_line,appmod_ns::MAX_STR_LEN,f));
                sscanf(st,"%s %s %s",source,dest,label);
                edges[i]=new Edge(std::string(source),std::string(dest),labels[atoi(label)]);

            }

            delete[] source;
            delete[] dest;
            delete[] label;

            /****************/
            int execution_length = atoi(fgets(the_line,appmod_ns::MAX_STR_LEN,f));
            /*****************************************************/
            Automata* fsa=new Automata(starting_nodes,ending_nodes,edges,labels,importances,execution_length);

            BehaviorModelChecker* bmodel =  new BehaviorModelChecker(fsa, process_id);

            fclose(f);
            delete[] the_line;
            return bmodel;
        }
        
        const char* get_package_name(const char* file_name, const char* ext, char* ans){
            strcpy(ans,file_name);
            ans[strlen(file_name)-strlen(ext)]='\0';
            return (const char*) ans;
        }

        void update_mapping(const char* pkg_name, BehaviorModelChecker* model){
             std::lock_guard<std::mutex> lock(this->mtx_mapping);
             (*this->mapping)[std::string(pkg_name)]=model;
        }

    public:

        BehaviorModelCollection(const char* model_folder){
            this->PATH_TO_MODEL_FOLDER = new std::string(model_folder);
            this->mapping = new std::map<std::string,BehaviorModelChecker*>();
        }

        BehaviorModelChecker* get_behavior_model(const char* the_package_name, pid_t process_id){
            // LOG(INFO) << StringPrintf("DUY_DEBUG: LOOKINF FOR MODEL \"%s\"", the_package_name);
            char* pid_str=new char[appmod_ns::MAX_STR_LEN];
            sprintf(pid_str,"%ld",(long) process_id);
            std::map<std::string,BehaviorModelChecker*>::iterator  it;
            {   
                std::lock_guard<std::mutex> lock(this->mtx_mapping);
                it=this->mapping->find(std::string(the_package_name));
            }
            if (it!=this->mapping->end()){
                BehaviorModelChecker*ans= it->second;
                if (strcmp(pid_str, ans->get_pid())!=0){
                    LOG(INFO) << StringPrintf("DUY_DEBUG: RESET MODEL \"%s\" %s %s %d", the_package_name, pid_str, ans->get_pid(),strcmp(pid_str, ans->get_pid()));
                    ans->reset();
                    ans->set_pid(process_id);
                }
                delete[] pid_str;
                return ans;
            }else{
                std::string file_name = (*this->PATH_TO_MODEL_FOLDER)+"/"+the_package_name+".txt";
                BehaviorModelChecker* model =this->parse_model(file_name.c_str(), process_id);               
                if (model!=NULL){
                    update_mapping(the_package_name, model);
                }
                delete[] pid_str;
                return model;
            }
        }


        ~BehaviorModelCollection(){
            for (std::map<std::string,BehaviorModelChecker*>::iterator it = this->mapping->begin();it!=this->mapping->end();it++){
                delete it->second;
            }
            delete this->mapping;
            delete this->PATH_TO_MODEL_FOLDER;
            LOG(INFO) << StringPrintf("DUY_DEBUG: BehaviorModelCollection is terminated.");
        }

};

namespace appmod_ns{
    const char* PATH_MODEL_FOLDER="/sdcard/appmod_models";
    BehaviorModelCollection collections(appmod_ns::PATH_MODEL_FOLDER);
}

/***********************************************************************************/

namespace appmod_ns{
    const char* CALCULATOR_APP="com.android.calculator2";
    const char* DIALER_APP="com.android.dialer";
    const char* CALENDAR_APP="com.android.calendar";
    const char* CONTACT_APP="com.android.contacts";
    const char* CALLER_APP="com.example.duy.caller";
    const char* CALLEE_APP="example.callee";
    const char* ZYGOTE_PROC="zygote";
    const char* INJECTED_METHOD="java.lang.String android.app.ActivityManager.getPackageNameForAppMod()";
    const char* currentApplication="android.app.Application android.app.ActivityThread.currentApplication()";
    const char* APPMOD_PKG="com.smu.appmod";
}

const char* find_application_name(pid_t pid){
    char* path = new char[appmod_ns::MAX_STR_LEN];
    sprintf(path, "/proc/%d/cmdline", pid);
    FILE *cmdline_file = fopen(path, "r");
    if (cmdline_file != NULL) {
        path=fgets(path,appmod_ns::MAX_STR_LEN,cmdline_file);      
        fclose(cmdline_file);
        if (path!=NULL){
            return trim(path);
        }else{
            delete[]path;
            return NULL;
        }
        
    }else{
        fclose(cmdline_file);
        delete[]path;
        return NULL;
    }
    
}

char* create_string(const char* st){
    char* ans = new char[strlen(st)];
    strcpy(ans,st);
    return ans;
}

void invoke_app(const char* appmod_app, const char* current_app){
    art::Thread* self =art::Thread::Current();
    JNIEnv* env = self->GetJniEnv();

    jclass activityThread = env->FindClass("android/app/ActivityThread");
    jmethodID currentActivityThread = env->GetStaticMethodID(activityThread, "currentActivityThread", "()Landroid/app/ActivityThread;");
    jobject at = env->CallStaticObjectMethod(activityThread, currentActivityThread);
    if (at==nullptr){
        LOG(INFO) << StringPrintf("DUY_DEBUG: APPMOD cannot find activityThread object");
        return;
    }
    jmethodID getApplication = env->GetMethodID(activityThread, "getApplication", "()Landroid/app/Application;");
    jobject context = env->CallObjectMethod(at, getApplication);
    if (context == nullptr){        
        LOG(INFO) << StringPrintf("DUY_DEBUG: APPMOD cannot find context object");
        return ;
    }

    jclass android_content_Context =env->FindClass("android/content/Context");
    jmethodID getPackageManager_method = env->GetMethodID(android_content_Context,"getPackageManager", "()Landroid/content/pm/PackageManager;");

    if (getPackageManager_method == nullptr){        
        LOG(INFO) << StringPrintf("DUY_DEBUG: APPMOD cannot find getPackageManager method");
        return ;
    }

    jobject pkg_manager_obj = env->CallObjectMethod(context,getPackageManager_method);

     if (pkg_manager_obj == nullptr){        
        LOG(INFO) << StringPrintf("DUY_DEBUG: APPMOD cannot find PackageManager object");
        return ;
    }

    jclass PackageManager_clazz =env->FindClass("android/content/pm/PackageManager");

    jmethodID getLaunchIntentForPackage_method = env->GetMethodID(PackageManager_clazz,"getLaunchIntentForPackage", "(Ljava/lang/String;)Landroid/content/Intent;");

    if (getLaunchIntentForPackage_method == nullptr){        
        LOG(INFO) << StringPrintf("DUY_DEBUG: APPMOD cannot find getLaunchIntentForPackage method");
        return ;
    }
 
    char* str = create_string(appmod_app);
    jstring jstr = env->NewStringUTF(str);

    jobject launch_intent_obj = (jobject) env->CallObjectMethod(pkg_manager_obj, getLaunchIntentForPackage_method, jstr);

    if (launch_intent_obj == nullptr){
        LOG(INFO) << StringPrintf("DUY_DEBUG: APPMOD cannot find intent object");   
        delete[] str;
        return ;
    }

    jclass intent_clazz = env->FindClass("android/content/Intent");

    if (intent_clazz == nullptr){
        LOG(INFO) << StringPrintf("DUY_DEBUG: APPMOD cannot find intent.putExtra clazz");   
        delete[] str;
        return ;
    }

    jmethodID putExtra_method = env->GetMethodID(intent_clazz,"putExtra","(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;");
    
    char* key_str = create_string("target_app");
    char* value_str = create_string(current_app);
    jstring key_jstring = env->NewStringUTF(key_str);
    jstring value_jstring = env->NewStringUTF(value_str);

    env->CallObjectMethod(launch_intent_obj,putExtra_method,key_jstring,value_jstring);

    jmethodID startActivity_method = env->GetMethodID(android_content_Context,"startActivity", "(Landroid/content/Intent;)V");

    if (startActivity_method == nullptr){
        LOG(INFO) << StringPrintf("DUY_DEBUG: APPMOD cannot find startActivity method");
        delete[] str;
        delete[] key_str;
        delete[] value_str;
        return ;
    }

    env->CallVoidMethod(context,startActivity_method,launch_intent_obj);
    LOG(INFO) << StringPrintf("DUY_DEBUG: APPMOD intent sent");
    delete[] str;
    delete[] key_str;
    delete[] value_str;
}

const char* jni_getpackage_name(){
    art::Thread* self =art::Thread::Current();
    JNIEnv* env = self->GetJniEnv();

    jclass activityThread = env->FindClass("android/app/ActivityThread");
    jmethodID currentActivityThread = env->GetStaticMethodID(activityThread, "currentActivityThread", "()Landroid/app/ActivityThread;");
    jobject at = env->CallStaticObjectMethod(activityThread, currentActivityThread);
    if (at==nullptr){
        return NULL;
    }
    jmethodID getApplication = env->GetMethodID(activityThread, "getApplication", "()Landroid/app/Application;");
    jobject context = env->CallObjectMethod(at, getApplication);
    if (context == nullptr){
        return NULL;
    }
    jclass android_content_Context =env->FindClass("android/content/Context");
    jmethodID midGetPackageName = env->GetMethodID(android_content_Context,"getPackageName", "()Ljava/lang/String;");
    jstring packageName= (jstring)env->CallObjectMethod(context, midGetPackageName);
    const char *strReturn = env->GetStringUTFChars(packageName, 0);
    return strReturn;
}



void appmod_guarding(const char* invoked_method, pid_t tid){

    pid_t pid = getpid();
    
    const char* pname = find_application_name(pid);
    BehaviorModelChecker* model_check = NULL;
    
    if (pname!=NULL)
    {   
        model_check = appmod_ns::collections.get_behavior_model(pname, pid);
        if (model_check!=NULL){    
            if (model_check->get_behavior_model()->get_vocabulary()->count(std::string(invoked_method))>0){
                LOG(INFO) << StringPrintf("DUY_DEBUG: FOUND MODEL \"%s\" invoked_method -> '%s' PID %u %u", pname!=NULL? pname:"NULL" ,invoked_method,pid,tid);
            }                                                                                                           
            
            bool is_benign=model_check->add_executed_method(tid, invoked_method);
            if (!is_benign){
                LOG(INFO) << StringPrintf("DUY_DEBUG: MALICIOUS -> CALL APPMOD \"%s\" invoked_method -> '%s' PID %u %u", pname!=NULL? pname:"NULL" , invoked_method,pid,tid);
                invoke_app(appmod_ns::APPMOD_PKG,pname);
            }
        }
        
    }
    
    delete pname;

}



    