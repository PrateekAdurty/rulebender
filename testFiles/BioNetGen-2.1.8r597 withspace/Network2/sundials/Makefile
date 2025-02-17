# -----------------------------------------------------------------
# $Revision: 1.5.2.3 $
# $Date: 2005/02/14 23:13:53 $
# -----------------------------------------------------------------
# Programmer(s): Radu Serban and Aaron Collier @ LLNL
# -----------------------------------------------------------------
# Copyright (c) 2002, The Regents of the University of California.
# Produced at the Lawrence Livermore National Laboratory.
# All rights reserved.
# For details, see sundials/shared/LICENSE.
# -----------------------------------------------------------------
# Main Makefile for SUNDIALS suite
#
# Makefile.  Generated from Makefile.in by configure.
# -----------------------------------------------------------------

SHELL = /bin/sh



srcdir       = .
abs_builddir = /Users/mr_smith22586/Documents/workspace/rulebender/distributionResources/BioNetGen/mac64/BioNetGen-2.1.8r597/Network2/sundials
prefix       = /Users/mr_smith22586/Documents/workspace/rulebender/distributionResources/BioNetGen/mac64/BioNetGen-2.1.8r597/Network2
exec_prefix  = ${prefix}
includedir   = ${prefix}/include
libdir       = ${exec_prefix}/lib

MODULES         = shared/source cvode/source cvode/fcmix
NVEC_MODULES    = nvec_ser
EXAMPLE_MODULES =  cvode/examples_ser

COMPONENTS = $(NVEC_MODULES) $(MODULES)
EXAMPLES   = $(EXAMPLE_MODULES)

FGREP   = grep -F
INSTALL = /usr/bin/install -c
CP_FILE = ${INSTALL} -m 644

all:
	@for i in ${COMPONENTS} ; do  \
	   if test -d $${i} ; then    \
	     echo "Making $${i}..." ; \
	     cd $${i} ;               \
	     ${MAKE} ;                \
	     cd ${abs_builddir} ;     \
	     echo "" ;                \
	   fi ;                       \
	 done

install:
	@for i in ${COMPONENTS} ; do          \
	   if test -d $${i} ; then            \
	     echo "Installing $${i}..." ;     \
	     cd $${i} ;                       \
	     ${MAKE} install ;                \
	     cd ${abs_builddir} ;             \
	     echo "" ;                        \
	   fi ;                               \
	 done

clean:
	@for i in ${COMPONENTS} ; do \
	   if test -d $${i} ; then   \
	     cd $${i} ;              \
	     ${MAKE} clean ;         \
	     cd ${abs_builddir} ;    \
	   fi ;                      \
	 done

examples:
	@if test "X${EXAMPLES}" = "Xno" ; then             \
	   echo "Examples were NOT configured" ;           \
	   echo "Rerun configure with --enable-examples" ; \
	 else                                              \
	   for i in ${EXAMPLES} ; do                       \
	     if test -d $${i} ; then                       \
	       echo "Making examples in $${i}..." ;        \
	       cd $${i} ;                                  \
	       ${MAKE} ;                                   \
	       cd ${abs_builddir} ;                        \
	       echo "" ;                                   \
	     fi ;                                          \
	   done ;                                          \
	 fi

examples_clean:
	@if test "X${EXAMPLES}" = "Xno" ; then \
	   : ;                                 \
	 else                                  \
	   for i in ${EXAMPLES} ; do           \
	     if test -d $${i} ; then           \
	       cd $${i} ;                      \
	       ${MAKE} clean ;                 \
	       cd ${abs_builddir} ;            \
	     fi ;                              \
	   done ;                              \
	 fi

examples_uninstall:
	@if test "X${EXAMPLES}" = "Xno" ; then \
	   : ;                                 \
	 else                                  \
	   for i in ${EXAMPLES} ; do           \
	     if test -d $${i} ; then           \
	       cd $${i} ;                      \
	       ${MAKE} uninstall ;             \
	       cd ${abs_builddir} ;            \
	     fi ;                              \
	   done ;                              \
	 fi

distclean: clean examples_clean
	@for i in ${COMPONENTS} ; do           \
	   if test -d $${i} ; then             \
	     rm -f $${i}/Makefile ;            \
	   fi ;                                \
	 done
	@if test "X${EXAMPLES}" = "Xno" ; then \
	   : ;                                 \
	 else                                  \
	   for i in ${EXAMPLES} ; do           \
	     if test -d $${i} ; then           \
	       rm -f $${i}/Makefile ;          \
	     fi ;                              \
	   done ;                              \
	 fi
	@rm -f config.log ;                    \
	 rm -f config.status ;                 \
	 rm -f config.cache ;                  \
	 rm -f config.h                        \
	 rm -f sundials_config.h ;             \
	 rm -f Makefile

uninstall: examples_uninstall
	@for i in ${COMPONENTS} ; do \
	   if test -d $${i} ; then   \
	     cd $${i} ;              \
	     ${MAKE} uninstall ;     \
	     cd ${abs_builddir} ;    \
	   fi ;                      \
	 done
