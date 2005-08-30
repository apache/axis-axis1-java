#!/bin/sh
#
# Driver script to run the interop 3 test clients against
# a list of endpoints (one for each test)
#
# Usage: run <endpointfile>
#
# Where <endpointfile> has the syntax
#   TESTNAME="<endpoint>"
#

if [ $# -ne 1 ]; then
    echo "Usage: run endpointfile"
    exit 1
fi

endpoints=$1

# set up endpoints
. $endpoints


# EmptySA
echo EmptySA
if [ ! -z "${Import1}" ]; then
   java test.wsdl.interop3.emptysa.EmptySATestCase ${EmptySA}
echo "======================================================="
fi

# Import1
echo Import1
if [ ! -z "${Import1}" ]; then
  java test.wsdl.interop3.import1.Import1TestCase ${Import1}
echo "======================================================="
fi

# Import2
echo Import2
if [ ! -z "${Import2}" ]; then
java test.wsdl.interop3.import2.Import2TestCase ${Import2}
echo "======================================================="
fi

# Import3
echo Import3
if [ ! -z "${Import3}" ]; then
java test.wsdl.interop3.import3.Import3TestCase ${Import3}
echo "======================================================="
fi

# Compound1
echo Compound1
if [ ! -z "${Compound1}" ]; then
java test.wsdl.interop3.compound1.Compound1TestCase ${Compound1}
echo "======================================================="
fi

# Compound2
echo Compound2
if [ ! -z "${Compound2}" ]; then
java test.wsdl.interop3.compound2.Compound2TestCase ${Compound2}
echo "======================================================="
fi

# DocLit
echo DocLit
if [ ! -z "${DocLit}" ]; then
java test.wsdl.interop3.docLit.DocLitTestCase ${DocLit}
echo "======================================================="
fi

# DocLitParam
echo DocLitParam
if [ ! -z "${DocLitParam}" ]; then
java test.wsdl.interop3.docLitParam.DocLitParamTestCase ${DocLitParam}
echo "======================================================="
fi

# RpcEnc
echo RpcEnc
if [ ! -z "${RpcEnc}" ]; then
java test.wsdl.interop3.rpcEnc.RpcEncTestCase ${RpcEnc}
echo "======================================================="
fi

# TestList
echo TestList
if [ ! -z "${TestList}" ]; then
java test.wsdl.interop3.groupE.client.InteropTestListServiceTestClient -l ${TestList}
echo "======================================================="
fi

# TestHeaders
#if [ ! -z "${TestHeaders}" ]; then
#java test.wsdl.interop3.emptySA.EmptySATestCase ${TestHeaders}
#echo "======================================================="
#fi


