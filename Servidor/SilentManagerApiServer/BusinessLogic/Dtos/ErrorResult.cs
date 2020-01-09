using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SilentManagerApiServer.BusinessLogic.Dtos
{
    public class ErrorResult
    {
        private int _code;
        private string _message;

        public int Code { get => _code; }
        public string Message { get => _message; }

        public ErrorResult(int code, string message)
        {
            this._code = code;
            this._message = message;
        }
    }
}
